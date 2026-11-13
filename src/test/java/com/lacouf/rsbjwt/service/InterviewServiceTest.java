package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.InterviewRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.service.dto.InterviewResponseDto;
import com.lacouf.rsbjwt.service.dto.InterviewSlotDto;
import com.lacouf.rsbjwt.service.dto.ScheduleInterviewRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private InterviewService interviewService;

    private JobApplication application;
    private static final String EMPLOYER_EMAIL = "employer@test.com";

    @BeforeEach
    void setUp() {
        Employer employer = new Employer();
        employer.setCredentials(Credentials.builder()
                .email(EMPLOYER_EMAIL).password("x").role(Role.EMPLOYER).build());

        JobOffer jobOffer = new JobOffer();
        jobOffer.setEmployer(employer);

        application = new JobApplication();
        application.setId(1L);
        application.setJobOffer(jobOffer);
    }

    private ScheduleInterviewRequestDto validOnlineDto() {
        return new ScheduleInterviewRequestDto(
                1L,
                InterviewType.ONLINE,
                "https://zoom.us/j/123",
                null,
                "Bring portfolio",
                List.of(new InterviewSlotDto(
                        LocalDate.of(2026, 11, 20),
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0)))
        );
    }

    @Test
    void schedule_success() {
        when(jobApplicationRepository.findByIdWithEmployer(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> {
            Interview i = inv.getArgument(0);
            i.setId(10L);
            return i;
        });

        InterviewResponseDto result = interviewService.schedule(validOnlineDto(), EMPLOYER_EMAIL);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(1L, result.candidatureId());
        assertEquals(InterviewType.ONLINE, result.type());
        assertEquals("https://zoom.us/j/123", result.videoLink());
        assertNull(result.address());
        assertEquals("Bring portfolio", result.notes());
        assertEquals(1, result.slots().size());
        verify(interviewRepository).save(any(Interview.class));
    }

    @Test
    void schedule_applicationNotFound() {
        when(jobApplicationRepository.findByIdWithEmployer(99L)).thenReturn(Optional.empty());

        ScheduleInterviewRequestDto dto = new ScheduleInterviewRequestDto(
                99L, InterviewType.ONLINE, "https://zoom.us/j/123", null, null,
                List.of(new InterviewSlotDto(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0))));

        assertThrows(ApplicationNotFoundException.class,
                () -> interviewService.schedule(dto, EMPLOYER_EMAIL));
        verify(interviewRepository, never()).save(any());
    }

    @Test
    void schedule_wrongEmployer() {
        when(jobApplicationRepository.findByIdWithEmployer(1L)).thenReturn(Optional.of(application));

        assertThrows(ApplicationNotFoundException.class,
                () -> interviewService.schedule(validOnlineDto(), "other@test.com"));
        verify(interviewRepository, never()).save(any());
    }

    @Test
    void schedule_onlineNullsAddress() {
        when(jobApplicationRepository.findByIdWithEmployer(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> {
            Interview i = inv.getArgument(0);
            i.setId(1L);
            return i;
        });

        InterviewResponseDto result = interviewService.schedule(new ScheduleInterviewRequestDto(
                        1L, InterviewType.ONLINE, "https://zoom.us", "Some address", null,
                        List.of(new InterviewSlotDto(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0)))),
                EMPLOYER_EMAIL);

        assertNull(result.address());
        assertEquals("https://zoom.us", result.videoLink());
    }

    @Test
    void schedule_inPersonNullsVideoLink() {
        when(jobApplicationRepository.findByIdWithEmployer(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> {
            Interview i = inv.getArgument(0);
            i.setId(1L);
            return i;
        });

        InterviewResponseDto result = interviewService.schedule(new ScheduleInterviewRequestDto(
                        1L, InterviewType.IN_PERSON, "https://zoom.us", "123 Main St", null,
                        List.of(new InterviewSlotDto(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0)))),
                EMPLOYER_EMAIL);

        assertNull(result.videoLink());
        assertEquals("123 Main St", result.address());
    }
}
