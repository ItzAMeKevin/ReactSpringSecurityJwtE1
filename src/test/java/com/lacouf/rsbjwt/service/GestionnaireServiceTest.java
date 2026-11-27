package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobApplicationSuiviMapper;
import com.lacouf.rsbjwt.mapper.CvMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.*;
import com.lacouf.rsbjwt.model.InternshipSiteEvaluation;
import com.lacouf.rsbjwt.repository.InternshipSiteEvaluationRepository;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionnaireServiceTest {

    @Mock private JobOfferRepository jobOfferRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private StudentCvRepository studentCvRepository;
    @Mock private JobApplicationRepository jobApplicationRepository;
    @Mock private CvMapper cvMapper;
    @Mock private JobOfferMapper jobOfferMapper;
    @Mock private JobApplicationSuiviMapper jobApplicationSuiviMapper;
    @Mock private InternshipSiteEvaluationRepository evaluationRepository;
    @InjectMocks private GestionnaireService service;

    @Test
    void getsPendingCvsAndContent() {
        StudentCv cv = new StudentCv();
        cv.setContent(new byte[]{1, 2});
        PendingCvDto dto = PendingCvDto.builder().id(1L).build();
        when(studentCvRepository.findAllByStatusOrderByUploadedAtAsc(Status.PENDING)).thenReturn(List.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(dto);

        assertEquals(List.of(dto), service.getPendingCvs());
        when(studentCvRepository.findById(1L)).thenReturn(Optional.of(cv));
        assertArrayEquals(new byte[]{1, 2}, service.getCvContent(1L));
    }

    @Test
    void rejectsMissingCvContent() {
        when(studentCvRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CvException.class, () -> service.getCvContent(1L));
    }

    @Test
    void acceptsAndDeclinesCvWithNotifications() {
        Student student = new Student();
        StudentCv cv = new StudentCv();
        cv.setId(4L);
        cv.setStudent(student);
        when(studentCvRepository.findById(4L)).thenReturn(Optional.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(PendingCvDto.builder().id(4L).build());

        assertEquals(4L, service.acceptCv(4L).getId());
        assertEquals(Status.ACCEPTED, cv.getStatus());
        verify(studentCvRepository).save(cv);
        verify(notificationRepository).save(any(Notification.class));

        FeedbackDto feedback = new FeedbackDto("Needs changes");
        service.declineCv(4L, feedback);
        assertEquals(Status.DECLINED, cv.getStatus());
        assertEquals("Needs changes", cv.getReviewFeedback());
        verify(studentCvRepository, times(2)).findById(4L);
    }

    @Test
    void changesOfferStatusAndRejectsInvalidOffers() throws Throwable {
        JobOffer offer = new JobOffer();
        offer.setId(8L);
        offer.setStatus(Status.PENDING);
        offer.setEmployer(new Employer());
        JobOfferDto dto = JobOfferDto.builder().id(8L).build();
        when(jobOfferRepository.findById(8L)).thenReturn(Optional.of(offer));
        when(jobOfferMapper.toDtoSimple(offer)).thenReturn(dto);

        assertEquals(dto, service.acceptOffer(8L));
        assertEquals(Status.ACCEPTED, offer.getStatus());

        assertThrows(InvalidStateTransitionException.class, () -> service.refuseOffer(8L, new FeedbackDto("feedback")));
        when(jobOfferRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(JobOfferNotFoundException.class, () -> service.acceptOffer(9L));
    }

    @Test
    void refusesOfferWithFeedback() throws Throwable {
        JobOffer offer = new JobOffer();
        offer.setId(10L);
        offer.setStatus(Status.PENDING);
        offer.setEmployer(new Employer());
        JobOfferDto dto = JobOfferDto.builder().id(10L).build();
        when(jobOfferRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(jobOfferMapper.toDtoSimple(offer)).thenReturn(dto);

        FeedbackDto feedback = new FeedbackDto("Not suitable");
        assertEquals(dto, service.refuseOffer(10L, feedback));
        assertEquals(Status.DECLINED, offer.getStatus());
        assertEquals("Not suitable", offer.getRefusalFeedback());
        verify(jobOfferRepository).save(offer);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void returnsAllCandidaturesAndEmptyList() {
        Student student = new Student();
        student.setFirstName("Jane");
        student.setLastName("Doe");
        student.setMatricule("1234567");
        student.setCredentials(Credentials.builder()
                .email("jane@test.com").password("x").role(Role.STUDENT).build());

        StudentCv cv = new StudentCv();
        cv.setStudent(student);

        Employer employer = new Employer();
        employer.setCompanyName("Acme");

        JobOffer offer = new JobOffer();
        offer.setTitle("Developer");
        offer.setEmployer(employer);
        offer.setDurationInWeeks(12);

        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setStudentCv(cv);
        application.setJobOffer(offer);
        application.setStatus(Status.PENDING);

        when(jobApplicationRepository.findAllWithStudentAndOffer()).thenReturn(List.of(application));
        StudentDetailsDto studentDetails =
                new StudentDetailsDto(2L, "Jane", "Doe", "1234567", "jane@test.com", Programe.TECHNIQUES_INFORMATIQUE);
        JobOfferDetailDTO offerDetails =
                JobOfferDetailDTO.builder().id(3L).title("Developer").companyName("Acme").durationInWeeks(12).build();
        when(jobApplicationSuiviMapper.toDtoList(List.of(application)))
                .thenReturn(List.of(new JobApplicationSuiviDto(
                        1L, Status.PENDING, null, null, studentDetails, offerDetails, null)));
        List<JobApplicationSuiviDto> result = service.getAllCandidatures();
        assertEquals(1, result.size());
        JobApplicationSuiviDto dto = result.get(0);
        assertEquals(1L, dto.id());
        assertEquals("Jane", dto.student().firstName());
        assertEquals("jane@test.com", dto.student().email());
        assertEquals("Acme", dto.jobOffer().getCompanyName());
        assertEquals(12, dto.jobOffer().getDurationInWeeks());
        assertEquals(Status.PENDING, dto.status());

        when(jobApplicationRepository.findAllWithStudentAndOffer()).thenReturn(List.of());
        assertTrue(service.getAllCandidatures().isEmpty());
    }

    // ── Internship Site Evaluation ────────────────────────────────────────────

    @Test
    void returnsPendingInternshipSitesWithCorrectData() {
        Student student = new Student();
        student.setFirstName("Alice");
        student.setLastName("Smith");
        student.setCredentials(Credentials.builder().email("alice@test.com").password("x").role(Role.STUDENT).build());

        Employer employer = new Employer();
        employer.setCompanyName("TechCorp");

        JobOffer offer = new JobOffer();
        offer.setEmployer(employer);
        offer.setStartingDate(LocalDate.of(2025, 1, 15));

        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setStudent(student);
        application.setJobOffer(offer);

        when(jobApplicationRepository.findByStatusWithoutEvaluation(Status.WORKING)).thenReturn(List.of(application));

        List<PendingInternshipSiteDto> result = service.getPendingInternshipSites();

        assertEquals(1, result.size());
        PendingInternshipSiteDto dto = result.get(0);
        assertEquals(1L, dto.id());
        assertEquals("Alice", dto.studentFirstName());
        assertEquals("Smith", dto.studentLastName());
        assertEquals("TechCorp", dto.companyName());
        assertEquals("2025-01-15", dto.startDate());
    }

    @Test
    void returnsNullStartDateWhenOfferHasNoStartDate() {
        Student student = new Student();
        student.setFirstName("Bob");
        student.setLastName("Jones");
        student.setCredentials(Credentials.builder().email("bob@test.com").password("x").role(Role.STUDENT).build());

        Employer employer = new Employer();
        employer.setCompanyName("Acme");

        JobOffer offer = new JobOffer();
        offer.setEmployer(employer);

        JobApplication application = new JobApplication();
        application.setId(2L);
        application.setStudent(student);
        application.setJobOffer(offer);

        when(jobApplicationRepository.findByStatusWithoutEvaluation(Status.WORKING)).thenReturn(List.of(application));

        assertNull(service.getPendingInternshipSites().get(0).startDate());
    }

    @Test
    void submitsEvaluationForWorkingApplication() {
        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setStatus(Status.WORKING);

        when(jobApplicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(evaluationRepository.existsByJobApplicationId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> service.submitInternshipSiteEvaluation(1L, emptyDto()));
        verify(evaluationRepository).save(any(InternshipSiteEvaluation.class));
    }

    @Test
    void rejectsEvaluationWhenApplicationNotFound() {
        when(jobApplicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> service.submitInternshipSiteEvaluation(99L, emptyDto()));
    }

    @Test
    void rejectsEvaluationWhenApplicationNotWorking() {
        JobApplication application = new JobApplication();
        application.setId(2L);
        application.setStatus(Status.PENDING);

        when(jobApplicationRepository.findById(2L)).thenReturn(Optional.of(application));

        assertThrows(InvalidStateTransitionException.class,
                () -> service.submitInternshipSiteEvaluation(2L, emptyDto()));
        verify(evaluationRepository, never()).save(any());
    }

    @Test
    void rejectsEvaluationWhenAlreadySubmitted() {
        JobApplication application = new JobApplication();
        application.setId(3L);
        application.setStatus(Status.WORKING);

        when(jobApplicationRepository.findById(3L)).thenReturn(Optional.of(application));
        when(evaluationRepository.existsByJobApplicationId(3L)).thenReturn(true);

        assertThrows(OfferAlreadyProcessedException.class,
                () -> service.submitInternshipSiteEvaluation(3L, emptyDto()));
        verify(evaluationRepository, never()).save(any());
    }

    private static InternshipSiteEvaluationDto emptyDto() {
        return new InternshipSiteEvaluationDto(
                null, null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null
        );
    }
}
