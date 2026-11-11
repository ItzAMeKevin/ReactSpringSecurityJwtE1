package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SystemeService systemeService;
    private final JobApplicationRepository jobApplicationRepository;
    private final StudentCvRepository studentCvRepository;
    private final CandidateMapper candidateMapper;

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
        systemeService.notifyManagers(NotificationType.JOB_OFFER, savedOffer.getId(), "notification.types.JOB_OFFER_CREATED.message");
        return jobOfferMapper.toDto(savedOffer);
    }

    public Employer findEmployerByEmail(String email) {
        return employerRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> new EmployerNotFoundException(email));
    }

    @Transactional
    public Optional<JobOfferDetailDTO> updateRefusedJobOffer(Long id, JobOfferCreateDTO dto, String employerEmail) {
        return jobOfferRepository.findByIdAndEmployerCredentialsEmail(id, employerEmail).map(offer -> {
            if (offer.getStatus() != Status.DECLINED) {
                throw new IllegalStateException("errors.jobOfferUpdateFailed");
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
            offer.setStatus(Status.PENDING);
            offer.setRefusalFeedback(null);
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
                candidates.add(candidateMapper.toCandidateDto(application));
            } else {
                candidates.add(candidateMapper.toCandidateDto(cv, cv.getUploadedAt(), Status.PENDING));
            }
        }
        applicationsByCvId.values().stream()
                .map(candidateMapper::toCandidateDto)
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
        return candidateMapper.toCandidateDto(jobApplicationRepository.save(application));
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
        return application;
    }



}
