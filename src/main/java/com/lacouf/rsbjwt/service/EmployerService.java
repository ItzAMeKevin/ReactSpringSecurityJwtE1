package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.ManagerNotification;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.ManagerNotificationRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lacouf.rsbjwt.model.Status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final JobOfferRepository jobOfferRepository;
    private final EmployerRepository employerRepository;
    private final JobOfferMapper jobOfferMapper;
    private final ManagerRepository managerRepository;
    private final ManagerNotificationRepository managerNotificationRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final StudentCvRepository studentCvRepository;

    public List<JobOfferDetailDTO> getJobOffres(String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        return jobOfferRepository.getJobOffersByEmployerId(employer.getId())
                .stream()
                .map(jobOfferMapper::toDto)
                .toList();
    }

    @Transactional
    public JobOfferDetailDTO addJobOffer(JobOfferCreateDTO jobOfferDTO, String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        JobOffer jobOfferEntity = jobOfferMapper.toEntity(jobOfferDTO);
        jobOfferEntity.setEmployer(employer);
        JobOffer savedOffer = jobOfferRepository.save(jobOfferEntity);
        notifyManagers(savedOffer);
        return jobOfferMapper.toDto(savedOffer);
    }

    private void notifyManagers(JobOffer offer) {
        try {
            List<Manager> managers = managerRepository.findAll();
            String companyName = offer.getEmployer() != null ? offer.getEmployer().getCompanyName() : "";
            String offerTitle = offer.getTitle() != null ? offer.getTitle() : "";
            String program = offer.getPrograme() != null ? offer.getPrograme().name() : "";
            List<ManagerNotification> notifs = managers.stream()
                    .map(manager -> new ManagerNotification(
                            "Nouvelle offre de stage",
                            "L'entreprise \"" + companyName
                                    + "\" a déposé une offre : \"" + offerTitle
                                    + "\" (" + program + ")",
                            offer.getId(),
                            manager,
                            companyName,
                            offerTitle,
                            program
                    ))
                    .toList();
            managerNotificationRepository.saveAll(notifs);
            managers.forEach(manager -> sendEmailStub(manager, offer));
        } catch (Exception e) {
            System.err.println("[NOTIFICATION-ERROR] Failed to notify managers: " + e.getMessage());
        }
    }

    private void sendEmailStub(Manager manager, JobOffer offer) {
        System.out.println("[EMAIL-STUB] To: " + manager.getCredentials().getEmail()
                + " | Offre: " + offer.getTitle()
                + " | Entreprise: " + offer.getEmployer().getCompanyName());
    }

    public Employer findEmployerByEmail(String email) {
        return employerRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> new EmployerNotFoundException(email));
    }

    @Transactional
    public Optional<JobOfferDetailDTO> updateRefusedJobOffer(Long id, JobOfferCreateDTO dto, String employerEmail) {
        return jobOfferRepository.findByIdAndEmployerCredentialsEmail(id, employerEmail).map(offer -> {
            if (offer.getStatus() != Status.DECLINED) {
                throw new IllegalStateException("Seules les offres refusées peuvent être modifiées.");
            }
            JobOffer updated = jobOfferMapper.toEntity(dto);
            offer.setTitle(updated.getTitle());
            offer.setDescription(updated.getDescription());
            offer.setPrerequisites(updated.getPrerequisites());
            offer.setSalary(updated.getSalary());
            offer.setStartingDate(updated.getStartingDate());
            offer.setDurationInWeeks(updated.getDurationInWeeks());
            offer.setPrograme(updated.getPrograme());
            offer.setAdresse(updated.getAdresse());
            offer.setStatus(Status.WAITING);
            return jobOfferMapper.toDto(jobOfferRepository.save(offer));
        });
    }

    @Transactional(readOnly = true)
    public List<CandidateDto> getCandidates(Long offerId, String employerEmail) {
        JobOffer offer = findOwnedOffer(offerId, employerEmail);
        Map<Long, JobApplication> applicationsByCvId = jobApplicationRepository.findByOfferIdWithStudent(offerId)
                .stream()
                .collect(Collectors.toMap(app -> app.getStudentCv().getId(), Function.identity(), (a, b) -> a));

        List<CandidateDto> candidates = new ArrayList<>();
        for (StudentCv cv : offer.getAppliedCvs()) {
            JobApplication application = applicationsByCvId.remove(cv.getId());
            if (application != null) {
                candidates.add(toCandidateDto(application));
            } else {
                candidates.add(toCandidateDto(cv, cv.getUploadedAt(), Status.PENDING));
            }
        }
        applicationsByCvId.values().stream()
                .map(this::toCandidateDto)
                .forEach(candidates::add);
        return candidates;
    }

    @Transactional
    public CandidateDto updateCandidateStatus(Long offerId, Long cvId, Status newStatus, String employerEmail) {
        if (newStatus != Status.ACCEPTED && newStatus != Status.DECLINED) {
            throw new IllegalArgumentException("Statut de candidature invalide.");
        }
        JobOffer offer = findOwnedOffer(offerId, employerEmail);
        JobApplication application = jobApplicationRepository.findByOfferIdAndCvIdWithStudent(offerId, cvId)
                .orElseGet(() -> createApplicationFromAppliedCv(offer, cvId));
        if (application.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Seules les candidatures en attente peuvent être mises à jour.");
        }
        application.setStatus(newStatus);
        return toCandidateDto(jobApplicationRepository.save(application));
    }

    @Transactional(readOnly = true)
    public byte[] getCandidateCv(Long cvId, String employerEmail) {
        boolean allowed = jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(cvId, employerEmail)
                || jobOfferRepository.existsByEmployer_Credentials_EmailAndAppliedCvs_Id(employerEmail, cvId);
        if (!allowed) {
            throw new ApplicationNotFoundException();
        }
        return studentCvRepository.findById(cvId)
                .map(StudentCv::getContent)
                .orElseThrow(() -> new InvalidCvException("CV not found"));
    }

    private JobOffer findOwnedOffer(Long offerId, String employerEmail) {
        return jobOfferRepository.findByIdAndEmployerCredentialsEmail(offerId, employerEmail)
                .orElseThrow(ApplicationNotFoundException::new);
    }

    private JobApplication createApplicationFromAppliedCv(JobOffer offer, Long cvId) {
        StudentCv cv = offer.getAppliedCvs().stream()
                .filter(appliedCv -> appliedCv.getId().equals(cvId))
                .findFirst()
                .orElseThrow(ApplicationNotFoundException::new);
        JobApplication application = new JobApplication();
        application.setJobOffer(offer);
        application.setStudentCv(cv);
        application.setStatus(Status.PENDING);
        return application;
    }

    private CandidateDto toCandidateDto(JobApplication application) {
        return toCandidateDto(application.getStudentCv(), application.getAppliedAt(), application.getStatus());
    }

    private CandidateDto toCandidateDto(StudentCv cv, Instant appliedAt, Status status) {
        Student student = cv.getStudent();
        return new CandidateDto(
                cv.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                appliedAt,
                status
        );
    }
}
