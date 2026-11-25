package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.CandidateMapper;
import com.lacouf.rsbjwt.mapper.CandidatureMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferUpdateException;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CandidateMapper candidateMapper;
    private final SystemeService systemeService;
    private final JobApplicationRepository jobApplicationRepository;
    private final StudentCvRepository studentCvRepository;
    private final CandidatureMapper candidatureMapper;

    @Transactional(readOnly = true)
    public List<JobOfferDetailDTO> getJobOffres(String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        List<JobOfferDetailDTO> dtos = jobOfferRepository.getJobOffersByEmployerId(employer.getId())
                .stream()
                .map(jobOfferMapper::toDto)
                .toList();
        if (dtos.isEmpty()) return dtos;
        List<Long> offerIds = dtos.stream().map(JobOfferDetailDTO::getId).toList();
        Map<Long, Long> hiredCounts = jobApplicationRepository.countByOfferIdsAndStatusIn(offerIds, JobApplication.HIRED_STATUSES)
                .stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
        return dtos.stream()
                .map(dto -> (JobOfferDetailDTO) dto.toBuilder()
                        .hiredCount(hiredCounts.getOrDefault(dto.getId(), 0L))
                        .build())
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
                throw new JobOfferUpdateException();
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
            offer.setNumberOfPositions(updated.getNumberOfPositions());
            offer.setStatus(Status.PENDING);
            offer.setRefusalFeedback(null);
            JobOffer savedOffer = jobOfferRepository.save(offer);
            systemeService.notifyManagers(NotificationType.JOB_OFFER, savedOffer.getId(),
                    "notification.types.JOB_OFFER_RESUBMITTED.message");
            return jobOfferMapper.toDto(savedOffer);
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
    public CandidateDto rejectCandidate(Long offerId, Long cvId, String employerEmail) {
        JobOffer offer = findOwnedOffer(offerId, employerEmail);
        JobApplication application = findOrCreateApplication(offer, cvId);
        application.reject();
        JobApplication saved = jobApplicationRepository.save(application);
        notifyStudent(saved, offer.getId(), "notification.types.JOB_OFFER_CANDIDATE_REJECTED.message");
        return candidateMapper.toCandidateDto(saved);
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
                .orElseThrow(() -> new CvException("errors.cvNotFound"));
    }

    @Transactional
    public CandidateDto hireStudent(Long offerId, Long cvId, String employerEmail) {
        JobOffer offer = jobOfferRepository.findOwnedOfferForUpdate(offerId, employerEmail)
                .orElseThrow(ApplicationNotFoundException::new);
        requireOpenPosition(offer);
        JobApplication application = findOrCreateApplication(offer, cvId);
        application.offerHire();
        JobApplication saved = jobApplicationRepository.save(application);
        notifyStudent(saved, saved.getId(), "notification.types.JOB_OFFER_HIRED.message");
        return candidateMapper.toCandidateDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CandidatureDto> getHirePendingCandidates(String employerEmail) {
        return candidatureMapper.toDtoList(
                jobApplicationRepository.findByEmployerEmailAndStatus(employerEmail, Status.HIRE_PENDING));
    }

    private void notifyStudent(JobApplication application, Long referenceId, String messageKey) {
        systemeService.notifyUsers(List.of(application.getStudentCv().getStudent()),
                NotificationType.JOB_OFFER, referenceId, messageKey);
    }

    private void requireOpenPosition(JobOffer offer) {
        if (offer.getStatus() != Status.ACCEPTED) {
            throw new InvalidStateTransitionException("errors.offerNotAccepted");
        }
        long heldSeats = jobApplicationRepository.countByJobOffer_IdAndStatusIn(offer.getId(), JobApplication.SEAT_HOLDING_STATUSES);
        if (heldSeats >= offer.getNumberOfPositions()) {
            throw new InvalidStateTransitionException("errors.noPositionsLeft");
        }
    }

    private JobApplication findOrCreateApplication(JobOffer offer, Long cvId) {
        return jobApplicationRepository.findByOfferIdAndCvIdWithStudent(offer.getId(), cvId)
                .orElseGet(() -> createApplicationFromAppliedCv(offer, cvId));
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
