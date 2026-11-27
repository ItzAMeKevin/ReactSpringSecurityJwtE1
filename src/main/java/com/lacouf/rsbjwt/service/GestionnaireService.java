package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferNotFoundException;
import com.lacouf.rsbjwt.mapper.CandidateMapper;
import com.lacouf.rsbjwt.mapper.CandidatureMapper;
import com.lacouf.rsbjwt.mapper.CvMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.mapper.ContractMapper;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.service.dto.ContractDto;
import com.lacouf.rsbjwt.service.dto.FeedbackDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.io.UncheckedIOException;

@Service
@RequiredArgsConstructor
public class GestionnaireService {

    private final JobOfferRepository jobOfferRepository;

    private final NotificationRepository notificationRepository;

    private final StudentCvRepository studentCvRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final CvMapper cvMapper;

    private final JobOfferMapper jobOfferMapper;

    private final CandidatureMapper candidatureMapper;

    private final ContractRepository contractRepository;

    private final ContractMapper contractMapper;

    private final StudentRepository studentRepository;

    private final EmployerRepository employerRepository;

    private final ManagerRepository managerRepository;


    public List<PendingCvDto> getPendingCvs() {
        return studentCvRepository.findAllByStatusOrderByUploadedAtAsc(Status.PENDING)
                .stream()
                .map(cvMapper::toDto)
                .toList();
    }

    public byte[] getCvContent(Long cvId) {
        return studentCvRepository.findById(cvId)
                .orElseThrow(() -> new CvException("errors.cvNotFound"))
                .getContent();
    }

    @Transactional
    public PendingCvDto acceptCv(Long cvId) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new CvException("errors.cvNotFound"));
        cv.setStatus(Status.ACCEPTED);
        studentCvRepository.save(cv);
        this.notificationRepository.save(Notification.builder()
                .message("notification.types.CV_ACCEPTED.message")
                .user(cv.getStudent())
                .notificationType(NotificationType.CV)
                .referenceId(cv.getId())
                .build());
        return cvMapper.toDto(cv);
    }

    @Transactional
    public void declineCv(Long cvId, FeedbackDto feedbackRequest) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new CvException("errors.cvNotFound"));
        cv.setStatus(Status.DECLINED);
        cv.setReviewFeedback(feedbackRequest.getFeedback());
        studentCvRepository.save(cv);
        this.notificationRepository.save(Notification.builder()
                .message("notification.types.CV_DECLINED.message")
                .user(cv.getStudent())
                .notificationType(NotificationType.CV)
                .referenceId(cv.getId())
                .build());
    }


    public List<JobOfferDto> getPendingOffers() {
        return jobOfferMapper.toDtoListJobOffers(jobOfferRepository.findAllByStatus(Status.PENDING));
    }


    @Transactional
    public JobOfferDto acceptOffer(Long id) throws JobOfferNotFoundException, InvalidStateTransitionException {
        return changeOfferStatus(id, Status.ACCEPTED, "notification.types.JOB_OFFER_ACCEPTED.message", null);
    }

    @Transactional
    public JobOfferDto refuseOffer(Long id, FeedbackDto feedbackRequest) throws JobOfferNotFoundException, InvalidStateTransitionException {
        return changeOfferStatus(id, Status.DECLINED, "notification.types.JOB_OFFER_DECLINED.message", feedbackRequest.getFeedback());
    }

    public List<CandidatureDto> getAllCandidatures() {
        return jobApplicationRepository.findAll().stream().map(candidatureMapper::toDto).collect(Collectors.toList());
    }

    private JobOfferDto changeOfferStatus(Long id, Status newStatus, String messageKey, String feedback) throws JobOfferNotFoundException, InvalidStateTransitionException {
        JobOffer offer = jobOfferRepository
                .findById(id)
                .orElseThrow(() -> new JobOfferNotFoundException("errors.jobOfferNotFound"));

        if (offer.getStatus() != Status.PENDING) {
            throw new InvalidStateTransitionException("errors.jobOfferInvalidState");
        }

        offer.setStatus(newStatus);
        if (feedback != null) {
            offer.setRefusalFeedback(feedback);
        }
        jobOfferRepository.save(offer);

        notificationRepository.save(Notification.builder()
                .message(messageKey)
                .user(offer.getEmployer())
                .notificationType(NotificationType.JOB_OFFER)
                .referenceId(offer.getId())
                .build());

        return jobOfferMapper.toDtoSimple(offer);
    }

    @Transactional
    public ContractDto createContract(CandidatureDto candidatureDto, Long managerId) {
        
        JobApplication jobApplication = jobApplicationRepository.findById(candidatureDto.id())
                .orElseThrow(ApplicationNotFoundException::new);

        Long jobOfferId = jobApplication.getJobOffer().getId();
        Long studentId = jobApplication.getStudent().getId();        
        Long employerId = jobApplication.getJobOffer().getEmployer().getId();

        JobOffer jobOffer = getJobOfferById(jobOfferId);
        Student student = getStudentById(studentId);
        Employer employer = getEmployerById(employerId);
        Manager manager = getManagerById(managerId);

        byte[] pdf;
        try {
            pdf = ContractPdfGenerator.generate(jobOffer, student, employer, manager);
        } catch (java.io.IOException exception) {
            throw new UncheckedIOException("Could not generate the internship contract PDF", exception);
        }

        Contract contract = new Contract();
        contract.setContent(pdf);
        contract.setFileName("contrat-stage-" + studentId + ".pdf");
        contract.setContentType("application/pdf");
        contract.setSize(pdf.length);
        contract.setStatus(Status.UNSIGNED);
        contract.setUploadedAt(java.time.Instant.now());
        contract.setStudent(student);
        contract.setEmployer(employer);
        contract.setManager(manager);
        contract.setSignedByStudent(false);
        contract.setSignedByEmployer(false);
        contract.setSignedByManager(false);

        student.setContract(contract);
        employer.setContract(contract);
        manager.setContract(contract);

        contract = contractRepository.save(contract);
        studentRepository.save(student);
        employerRepository.save(employer);
        managerRepository.save(manager);

        return contractMapper.toDto(contract);

    }

    private Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Employer getEmployerById(Long employerId) {
        return employerRepository.findById(employerId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Manager getManagerById(Long managerId) {
        return managerRepository.findById(managerId)
                .orElseThrow(UserNotFoundException::new);
    }

    private JobOffer getJobOfferById(Long jobOfferId) {
        return jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new JobOfferNotFoundException("errors.jobOfferNotFound"));
    }
}
