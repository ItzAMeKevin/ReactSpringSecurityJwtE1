package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.model.Interview;
import com.lacouf.rsbjwt.model.InterviewSlot;
import com.lacouf.rsbjwt.model.InterviewType;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.repository.InterviewRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.service.dto.InterviewResponseDto;
import com.lacouf.rsbjwt.service.dto.InterviewSlotDto;
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

    @Transactional
    public InterviewResponseDto schedule(ScheduleInterviewRequestDto dto, String employerEmail) {
        JobApplication application = jobApplicationRepository.findByIdWithEmployer(dto.candidatureId())
                .orElseThrow(ApplicationNotFoundException::new);

        String offerOwnerEmail = application.getJobOffer()
                .getEmployer().getCredentials().getEmail();
        if (!offerOwnerEmail.equals(employerEmail)) {
            throw new ApplicationNotFoundException();
        }

        Interview interview = new Interview();
        interview.setJobApplication(application);
        interview.setType(dto.type());
        interview.setVideoLink(dto.type() == InterviewType.ONLINE ? dto.videoLink() : null);
        interview.setAddress(dto.type() == InterviewType.IN_PERSON ? dto.address() : null);
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
        return toDto(saved);
    }

    private InterviewResponseDto toDto(Interview i) {
        return new InterviewResponseDto(
                i.getId(),
                i.getJobApplication().getId(),
                i.getType(),
                i.getVideoLink(),
                i.getAddress(),
                i.getNotes(),
                i.getScheduledAt(),
                i.getSlots().stream()
                        .map(s -> new InterviewSlotDto(s.getDate(), s.getStartTime(), s.getEndTime()))
                        .toList()
        );
    }
}

