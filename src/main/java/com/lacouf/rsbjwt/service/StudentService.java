package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.mapper.InterviewMapper;
import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.mapper.CandidatureMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Interview;
import com.lacouf.rsbjwt.model.InterviewSlot;
import com.lacouf.rsbjwt.model.InterviewStatus;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.repository.InterviewRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.InterviewDto;
import com.lacouf.rsbjwt.service.dto.InterviewRespondRequest;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JobOfferRepository jobOfferRepository;
    private final StudentRepository studentRepository;
    private final JobOfferMapper jobOfferMapper;
    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewMapper interviewMapper;
    private final InterviewRepository interviewRepository;
    private final CandidatureMapper candidatureMapper;
    private final SystemeService systemeService;

    public List<JobOfferDetailDTO> getAcceptedJobOffers(String studentEmail) throws UserNotFoundException {
        Student student = studentRepository.findByCredentialsEmail(studentEmail)
                .orElseThrow(() -> new UserNotFoundException());

        Map<Long, Status> applicationStatuses = jobApplicationRepository
                .findByStudentIdWithOffer(student.getId())
                .stream()
                .collect(Collectors.toMap(
                        ja -> ja.getJobOffer().getId(),
                        JobApplication::getStatus,
                        (mostRecent, older) -> mostRecent
                ));

        return jobOfferRepository.findAllByStatusInAndPrograme(List.of(Status.ACCEPTED, Status.FILLED), student.getPrograme())
                .stream()
                .map(offer -> jobOfferMapper.toDto(offer)
                        .withApplication(
                                applicationStatuses.containsKey(offer.getId()),
                                applicationStatuses.get(offer.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CandidatureDto> getPendingHireOffers(String studentEmail) {
        return candidatureMapper.toDtoList(
                jobApplicationRepository.findByStudentEmailAndStatus(studentEmail, Status.HIRE_PENDING));
    }

    @Transactional
    public CandidatureDto acceptHire(Long applicationId, String studentEmail) {
        JobApplication application = findOwnHire(applicationId, studentEmail);
        JobOffer offer = jobOfferRepository.findByIdForUpdate(application.getJobOffer().getId())
                .orElseThrow(ApplicationNotFoundException::new);
        application.acceptHire();
        jobApplicationRepository.save(application);
        fillOfferIfComplete(offer);
        notifyEmployer(application, "notification.types.JOB_OFFER_HIRE_ACCEPTED.message");
        return candidatureMapper.toCandidatureDto(application);
    }

    @Transactional
    public CandidatureDto declineHire(Long applicationId, String studentEmail) {
        JobApplication application = findOwnHire(applicationId, studentEmail);
        application.declineHire();
        jobApplicationRepository.save(application);
        notifyEmployer(application, "notification.types.JOB_OFFER_HIRE_DECLINED.message");
        return candidatureMapper.toCandidatureDto(application);
    }

    private JobApplication findOwnHire(Long applicationId, String studentEmail) {
        return jobApplicationRepository.findByIdAndStudentEmail(applicationId, studentEmail)
                .orElseThrow(ApplicationNotFoundException::new);
    }

    private void fillOfferIfComplete(JobOffer offer) {
        long hiredCount = jobApplicationRepository.countByJobOffer_IdAndStatusIn(offer.getId(), JobApplication.HIRED_STATUSES);
        if (hiredCount >= offer.getNumberOfPositions()) {
            offer.setStatus(Status.FILLED);
            jobOfferRepository.save(offer);
        }
    }

    private void notifyEmployer(JobApplication application, String messageKey) {
        JobOffer offer = application.getJobOffer();
        systemeService.notifyUsers(List.of(offer.getEmployer()), NotificationType.JOB_OFFER, offer.getId(), messageKey);
    }

    public List<InterviewDto> getInterviews(Authentication authentication) {
        String email = authentication.getName();
        return interviewRepository
                .findByJobApplication_Student_Credentials_Email(email)
                .stream()
                .map(this::withExpired)
                .toList();
    }

    @Transactional
    public InterviewDto respondToInterview(Long interviewId, InterviewRespondRequest request, Authentication authentication) {
        String email = authentication.getName();
        var interview = getInterview(interviewId, email);
        if (interview.getStatus() != InterviewStatus.PENDING) {
            throw new ApplicationNotFoundException();
        }

        selectChosenSlot(interview, request);
        interview.setStatus(request.status());
        return withExpired(interviewRepository.save(interview));
    }

    private void selectChosenSlot(Interview interview, InterviewRespondRequest request) {
        interview.getSlots().forEach(s -> s.setSelected(false));
        if (request.status() == InterviewStatus.ACCEPTED) {
            InterviewSlot chosen = interview.getSlots().stream()
                    .filter(s -> s.getId().equals(request.selectedSlotId()))
                    .findFirst()
                    .orElseThrow(ApplicationNotFoundException::new);
            chosen.setSelected(true);
        }
    }

    private @NonNull Interview getInterview(Long interviewId, String email) {
        var interview = interviewRepository
                .findByIdAndJobApplication_Student_Credentials_Email(interviewId, email)
                .orElseThrow(ApplicationNotFoundException::new);
        return interview;
    }

    private InterviewDto withExpired(Interview interview) {
        InterviewDto dto = interviewMapper.toInterviewDto(interview);
        boolean expired = interview.getStatus() == InterviewStatus.PENDING
                && !interview.getSlots().isEmpty()
                && interview.getSlots().stream().allMatch(s -> s.getDate().isBefore(LocalDate.now()));
        return new InterviewDto(dto.id(), dto.candidatureId(), dto.companyName(), dto.offerTitle(),
                dto.type(), dto.videoLink(), dto.address(), dto.notes(), dto.scheduledAt(),
                dto.slots(), dto.status(), dto.studentMessage(), expired);
    }
}
