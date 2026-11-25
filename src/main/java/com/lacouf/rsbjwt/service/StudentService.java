package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.ApplicationStateException;
import com.lacouf.rsbjwt.mapper.InterviewMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Interview;
import com.lacouf.rsbjwt.model.InterviewSlot;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.repository.InterviewRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.InterviewDto;
import com.lacouf.rsbjwt.service.dto.InterviewRespondRequest;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
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
    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;

    @Transactional(readOnly = true)
    public List<JobOfferDetailDTO> getAcceptedJobOffers(String studentEmail) throws UserNotFoundException {
        Student student = studentRepository.findByCredentialsEmail(studentEmail)
                .orElseThrow(UserNotFoundException::new);

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
    public List<InterviewDto> getInterviews(String studentEmail) {
        return interviewRepository.findAllByStudentEmail(studentEmail).stream()
                .map(this::withExpired)
                .toList();
    }

    @Transactional
    public InterviewDto respondToInterview(Long interviewId, InterviewRespondRequest request, String studentEmail) {
        Interview interview = interviewRepository.findByIdAndStudentEmail(interviewId, studentEmail)
                .orElseThrow(ApplicationNotFoundException::new);

        if (interview.getStatus() != Status.PENDING) {
            throw new ApplicationStateException();
        }

        if (request.status() == Status.ACCEPTED) {
            InterviewSlot chosen = interview.getSlots().stream()
                    .filter(s -> s.getId().equals(request.selectedSlotId()))
                    .findFirst()
                    .orElseThrow(ApplicationNotFoundException::new);
            chosen.setSelected(true);
        }

        interview.setStatus(request.status());
        interview.setStudentMessage(request.message());
        return withExpired(interviewRepository.save(interview));
    }

    private InterviewDto withExpired(Interview interview) {
        InterviewDto dto = interviewMapper.toInterviewDto(interview);
        boolean expired = interview.getStatus() == Status.PENDING
                && !interview.getSlots().isEmpty()
                && interview.getSlots().stream().allMatch(s -> s.getDate().isBefore(LocalDate.now()));
        return new InterviewDto(dto.id(), dto.candidatureId(), dto.companyName(), dto.offerTitle(),
                dto.type(), dto.videoLink(), dto.address(), dto.notes(), dto.scheduledAt(),
                dto.slots(), dto.status(), dto.studentMessage(), expired);
    }
}
