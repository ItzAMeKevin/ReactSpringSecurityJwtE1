package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.OfferAlreadyProcessedException;
import com.lacouf.rsbjwt.mapper.AdresseMapper;
import com.lacouf.rsbjwt.mapper.InterviewMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.InterviewRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.EmployerInterviewDto;
import com.lacouf.rsbjwt.service.dto.InterviewResponseDto;
import com.lacouf.rsbjwt.service.dto.InterviewSlotDto;
import com.lacouf.rsbjwt.service.dto.ScheduleInterviewRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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

    @Mock
    private AdresseMapper adresseMapper;

    @Mock
    private InterviewMapper interviewMapper;

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
        when(interviewMapper.toDto(any())).thenReturn(
                new InterviewResponseDto(10L, 1L, InterviewType.ONLINE, "https://zoom.us/j/123", null, "Bring portfolio", Instant.now(), List.of()));

        InterviewResponseDto result = interviewService.schedule(validOnlineDto(), EMPLOYER_EMAIL);

        assertNotNull(result);
        verify(interviewRepository).save(any(Interview.class));
        verify(interviewMapper).toDto(any(Interview.class));
        verify(jobApplicationRepository).updateStatus(1L, Status.INTERVIEW_SCHEDULED);
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
    void schedule_duplicateApplication_throws409() {
        when(jobApplicationRepository.findByIdWithEmployer(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.existsByJobApplicationId(1L)).thenReturn(true);

        assertThrows(OfferAlreadyProcessedException.class,
                () -> interviewService.schedule(validOnlineDto(), EMPLOYER_EMAIL));
        verify(interviewRepository, never()).save(any());
    }

    @Test
    void schedule_onlineNullsAddress() {
        when(jobApplicationRepository.findByIdWithEmployer(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interviewMapper.toDto(any())).thenReturn(
                new InterviewResponseDto(1L, 1L, InterviewType.ONLINE, "https://zoom.us", null, null, Instant.now(), List.of()));

        interviewService.schedule(new ScheduleInterviewRequestDto(
                        1L, InterviewType.ONLINE, "https://zoom.us", null, null,
                        List.of(new InterviewSlotDto(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0)))),
                EMPLOYER_EMAIL);

        ArgumentCaptor<Interview> captor = ArgumentCaptor.forClass(Interview.class);
        verify(interviewRepository).save(captor.capture());
        assertNull(captor.getValue().getAdresse());
        assertEquals("https://zoom.us", captor.getValue().getVideoLink());
    }

    @Test
    void schedule_inPersonNullsVideoLink() {
        AdresseDTO adresseDto = new AdresseDTO("Canada", "Montréal", "St-Denis", "123", "H2X 1A1");
        Adresse adresse = Adresse.builder().pays("Canada").ville("Montréal").rue("St-Denis").numeroCivic("123").codePostal("H2X 1A1").build();
        when(adresseMapper.toEntity(adresseDto)).thenReturn(adresse);
        when(jobApplicationRepository.findByIdWithEmployer(1L)).thenReturn(Optional.of(application));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interviewMapper.toDto(any())).thenReturn(
                new InterviewResponseDto(1L, 1L, InterviewType.IN_PERSON, null, adresseDto, null, Instant.now(), List.of()));

        interviewService.schedule(new ScheduleInterviewRequestDto(
                        1L, InterviewType.IN_PERSON, "https://zoom.us", adresseDto, null,
                        List.of(new InterviewSlotDto(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0)))),
                EMPLOYER_EMAIL);

        ArgumentCaptor<Interview> captor = ArgumentCaptor.forClass(Interview.class);
        verify(interviewRepository).save(captor.capture());
        assertNull(captor.getValue().getVideoLink());
        assertSame(adresse, captor.getValue().getAdresse());
    }

    @Test
    void getEmployerInterviews_mapsEachInterview() {
        Interview first = new Interview();
        Interview second = new Interview();
        EmployerInterviewDto firstDto = employerInterviewDto(1L);
        EmployerInterviewDto secondDto = employerInterviewDto(2L);
        when(interviewRepository.findAllByEmployerEmail(EMPLOYER_EMAIL)).thenReturn(List.of(first, second));
        when(interviewMapper.toEmployerDto(first)).thenReturn(firstDto);
        when(interviewMapper.toEmployerDto(second)).thenReturn(secondDto);

        List<EmployerInterviewDto> result = interviewService.getEmployerInterviews(EMPLOYER_EMAIL);

        assertEquals(List.of(firstDto, secondDto), result);
    }

    @Test
    void getEmployerInterviews_returnsEmptyListWhenNone() {
        when(interviewRepository.findAllByEmployerEmail(EMPLOYER_EMAIL)).thenReturn(List.of());

        List<EmployerInterviewDto> result = interviewService.getEmployerInterviews(EMPLOYER_EMAIL);

        assertTrue(result.isEmpty());
        verify(interviewMapper, never()).toEmployerDto(any());
    }

    private EmployerInterviewDto employerInterviewDto(Long id) {
        return new EmployerInterviewDto(id, 1L, 3L, "Développeur Java", "Jean", "Dupont", "jean@test.com",
                InterviewType.ONLINE, "https://zoom.us/j/123", null, null, Instant.now(), List.of());
    }
}
