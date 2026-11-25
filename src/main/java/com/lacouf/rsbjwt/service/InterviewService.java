package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.OfferAlreadyProcessedException;
import com.lacouf.rsbjwt.mapper.AdresseMapper;
import com.lacouf.rsbjwt.mapper.InterviewMapper;
import com.lacouf.rsbjwt.model.Interview;
import com.lacouf.rsbjwt.model.InterviewSlot;
import com.lacouf.rsbjwt.model.InterviewType;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.repository.InterviewRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.service.dto.EmployerInterviewDto;
import com.lacouf.rsbjwt.service.dto.InterviewResponseDto;
import com.lacouf.rsbjwt.service.dto.ScheduleInterviewRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AdresseMapper adresseMapper;
    private final InterviewMapper interviewMapper;

    @Transactional
    public InterviewResponseDto schedule(ScheduleInterviewRequestDto dto, String employerEmail) {
        JobApplication application = jobApplicationRepository.findByIdWithEmployer(dto.candidatureId())
                .orElseThrow(ApplicationNotFoundException::new);

        String offerOwnerEmail = application.getJobOffer()
                .getEmployer().getCredentials().getEmail();
        if (!offerOwnerEmail.equals(employerEmail)) {
            throw new ApplicationNotFoundException();
        }

        if (interviewRepository.existsByJobApplicationId(dto.candidatureId())) {
            throw new OfferAlreadyProcessedException("errors.interviewAlreadyScheduled");
        }

        Interview interview = new Interview();
        interview.setJobApplication(application);
        interview.setType(dto.type());
        interview.setVideoLink(dto.type() == InterviewType.ONLINE ? dto.videoLink() : null);
        interview.setAdresse(dto.type() == InterviewType.IN_PERSON ? adresseMapper.toEntity(dto.address()) : null);
        interview.setNotes(dto.notes());

        List<InterviewSlot> slots = dto.slots().stream().map(s -> {
            InterviewSlot slot = new InterviewSlot();
            slot.setInterview(interview);
            slot.setDate(s.date());
            slot.setStartTime(s.startTime());
            slot.setEndTime(s.endTime());
            return slot;
        }).toList();
        interview.setSlots(slots);

        Interview saved = interviewRepository.save(interview);
        jobApplicationRepository.updateStatus(application.getId(), Status.INTERVIEW_SCHEDULED);
        return interviewMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EmployerInterviewDto> getEmployerInterviews(String employerEmail) {
        return interviewRepository.findAllByEmployerEmail(employerEmail).stream()
                .map(interviewMapper::toEmployerDto)
                .toList();
    }
}

