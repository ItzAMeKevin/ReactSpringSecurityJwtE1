package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.security.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    @Mock private JobOfferRepository jobOfferRepository;
    @Mock private EmployerRepository employerRepository;
    @Mock private JobOfferMapper jobOfferMapper;
    @Mock private ManagerRepository managerRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private JobApplicationRepository jobApplicationRepository;
    @Mock private StudentCvRepository studentCvRepository;
    @InjectMocks private EmployerService service;

    private JobOffer jobOffer;

    @BeforeEach
    void setUp() {
        jobOffer = new JobOffer();
        jobOffer.setId(1L);
        jobOffer.setTitle("Développeur Java");
    }

    private Employer employer() {
        Employer employer = new Employer();
        employer.setId(1L);
        employer.setCompanyName("ACME");
        return employer;
    }

    private JobOfferCreateDTO request() {
        return new JobOfferCreateDTO("Java", "Build APIs", "Spring", null, "60000",
                LocalDate.now(), 12, Programe.TECHNIQUES_INFORMATIQUE);
    }

    @Test
    void getsOffersAndMapsThem() {
        Employer employer = employer();
        JobOffer offer = new JobOffer();
        JobOfferDetailDTO dto = new JobOfferDetailDTO(1L, "Java Dev", "Desc", "Prereq", null, "60000", Status.ACCEPTED, null, null, 12, Programe.TECHNIQUES_INFORMATIQUE, "ACME");
        when(employerRepository.findByCredentialsEmail("employer@test.com")).thenReturn(Optional.of(employer));
        when(jobOfferRepository.getJobOffersByEmployerId(1L)).thenReturn(List.of(offer));
        when(jobOfferMapper.toDto(offer)).thenReturn(dto);

        assertEquals(List.of(dto), service.getJobOffres("employer@test.com"));
    }

    @Test
    void addsOfferAndNotification() {
        Employer employer = employer();
        JobOffer offer = new JobOffer();
        offer.setId(3L);
        when(employerRepository.findByCredentialsEmail("employer@test.com")).thenReturn(Optional.of(employer));
        when(jobOfferMapper.toEntity(any(JobOfferCreateDTO.class))).thenReturn(offer);
        when(jobOfferRepository.save(offer)).thenReturn(offer);
        JobOfferDetailDTO result = new JobOfferDetailDTO(3L, "Java", "Desc", "Prereq", null, "60000", Status.PENDING, null, null, 12, Programe.TECHNIQUES_INFORMATIQUE, "ACME");
        when(jobOfferMapper.toDto(offer)).thenReturn(result);

        assertEquals(3L, service.addJobOffer(request(), "employer@test.com").id());
        assertSame(employer, offer.getEmployer());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void rejectsUnknownEmployer() {
        when(employerRepository.findByCredentialsEmail("missing@test.com")).thenReturn(Optional.empty());
        assertThrows(EmployerNotFoundException.class,
                () -> service.getJobOffres("missing@test.com"));
    }

    @Test
    void updatesOnlyDeclinedOffer() {
        Employer employer = employer();
        JobOffer offer = new JobOffer();
        offer.setStatus(Status.DECLINED);
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "employer@test.com"))
                .thenReturn(Optional.of(offer));
        when(jobOfferMapper.toEntity(any(JobOfferCreateDTO.class))).thenReturn(new JobOffer());
        when(jobOfferRepository.save(offer)).thenReturn(offer);
        when(jobOfferMapper.toDto(offer)).thenReturn(new JobOfferDetailDTO(null, null, null, null, null, null, null, null, null, null, null, null));

        assertTrue(service.updateRefusedJobOffer(1L, request(), "employer@test.com").isPresent());
        assertEquals(Status.PENDING, offer.getStatus());

        offer.setStatus(Status.ACCEPTED);
        assertThrows(IllegalStateException.class,
                () -> service.updateRefusedJobOffer(1L, request(), "employer@test.com"));
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(2L, "employer@test.com"))
                .thenReturn(Optional.empty());
        assertTrue(service.updateRefusedJobOffer(2L, request(), "employer@test.com").isEmpty());
    }

    @Test
    void getCandidates_emptyList() {
        String email = "marie@acme.com";
        jobOffer.setAppliedCvs(new ArrayList<>());
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, email)).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdWithStudent(1L)).thenReturn(List.of());

        List<CandidateDto> result = service.getCandidates(1L, email);

        assertTrue(result.isEmpty());
    }

    @Test
    void getCandidates_offerNotOwned_throws() {
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "other@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> service.getCandidates(1L, "other@test.com"));
    }

    @Test
    void getCandidates_mapsCandidateInfo() {
        String email = "marie@acme.com";
        Student student = new Student();
        student.setFirstName("Jean");
        student.setLastName("Dupont");
        student.setCredentials(Credentials.builder()
                .email("jean@test.com").password("x").role(Role.STUDENT).build());

        StudentCv cv = new StudentCv();
        cv.setId(5L);
        cv.setStudent(student);
        cv.setUploadedAt(Instant.parse("2026-01-15T10:00:00Z"));
        jobOffer.setAppliedCvs(new ArrayList<>(List.of(cv)));

        JobApplication application = new JobApplication();
        application.setStudentCv(cv);
        application.setAppliedAt(Instant.parse("2026-02-01T12:00:00Z"));
        application.setStatus(Status.PENDING);

        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, email)).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdWithStudent(1L)).thenReturn(List.of(application));

        List<CandidateDto> result = service.getCandidates(1L, email);

        assertEquals(1, result.size());
        CandidateDto dto = result.getFirst();
        assertEquals(5L, dto.cvId());
        assertEquals("Jean", dto.firstName());
        assertEquals("Dupont", dto.lastName());
        assertEquals("jean@test.com", dto.email());
        assertEquals(Instant.parse("2026-02-01T12:00:00Z"), dto.appliedAt());
        assertEquals(Status.PENDING, dto.status());
    }

    @Test
    void updateCandidateStatus_acceptsPending() {
        String email = "marie@acme.com";
        Student student = new Student();
        student.setFirstName("Jean");
        student.setLastName("Dupont");
        student.setCredentials(Credentials.builder()
                .email("jean@test.com").password("x").role(Role.STUDENT).build());
        StudentCv cv = new StudentCv();
        cv.setId(5L);
        cv.setStudent(student);

        JobApplication application = new JobApplication();
        application.setStudentCv(cv);
        application.setStatus(Status.PENDING);
        application.setAppliedAt(Instant.parse("2026-02-01T12:00:00Z"));

        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, email)).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.of(application));
        when(jobApplicationRepository.save(application)).thenReturn(application);

        CandidateDto result = service.updateCandidateStatus(1L, 5L, Status.ACCEPTED, email);

        assertEquals(Status.ACCEPTED, result.status());
        assertEquals(Status.ACCEPTED, application.getStatus());
    }

    @Test
    void updateCandidateStatus_invalidStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateCandidateStatus(1L, 5L, Status.READ, "marie@acme.com"));
        verifyNoInteractions(jobApplicationRepository);
    }

    @Test
    void getCandidateCv_notOwned_throws() {
        when(jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(5L, "marie@acme.com"))
                .thenReturn(false);
        when(jobOfferRepository.existsByEmployer_Credentials_EmailAndAppliedCvs_Id("marie@acme.com", 5L))
                .thenReturn(false);

        assertThrows(ApplicationNotFoundException.class,
                () -> service.getCandidateCv(5L, "marie@acme.com"));
        verify(studentCvRepository, never()).findById(any());
    }

    @Test
    void getCandidateCv_returnsContent() {
        byte[] pdf = {37, 80, 68, 70};
        StudentCv cv = new StudentCv();
        cv.setContent(pdf);
        when(jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(5L, "marie@acme.com"))
                .thenReturn(true);
        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));

        assertArrayEquals(pdf, service.getCandidateCv(5L, "marie@acme.com"));
    }

    private StudentCv cvWithStudent(Long id) {
        Student student = new Student();
        student.setFirstName("Jean");
        student.setLastName("Dupont");
        student.setCredentials(Credentials.builder()
                .email("jean@test.com").password("x").role(Role.STUDENT).build());
        StudentCv cv = new StudentCv();
        cv.setId(id);
        cv.setStudent(student);
        return cv;
    }

    @Test
    void getCandidates_cvWithoutApplication_defaultsToPending() {
        StudentCv cv = cvWithStudent(5L);
        cv.setUploadedAt(Instant.parse("2026-01-15T10:00:00Z"));
        jobOffer.setAppliedCvs(new ArrayList<>(List.of(cv)));
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdWithStudent(1L)).thenReturn(List.of());

        List<CandidateDto> result = service.getCandidates(1L, "marie@acme.com");

        assertEquals(1, result.size());
        assertEquals(Status.PENDING, result.getFirst().status());
        assertEquals(Instant.parse("2026-01-15T10:00:00Z"), result.getFirst().appliedAt());
    }

    @Test
    void getCandidates_applicationWithoutAppliedCv_isStillListed() {
        jobOffer.setAppliedCvs(new ArrayList<>());
        JobApplication application = new JobApplication();
        application.setStudentCv(cvWithStudent(5L));
        application.setStatus(Status.ACCEPTED);
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdWithStudent(1L)).thenReturn(List.of(application));

        List<CandidateDto> result = service.getCandidates(1L, "marie@acme.com");

        assertEquals(1, result.size());
        assertEquals(Status.ACCEPTED, result.getFirst().status());
    }

    @Test
    void updateCandidateStatus_notPending_throwsIllegalState() {
        JobApplication application = new JobApplication();
        application.setStudentCv(cvWithStudent(5L));
        application.setStatus(Status.ACCEPTED);
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.of(application));

        assertThrows(IllegalStateException.class,
                () -> service.updateCandidateStatus(1L, 5L, Status.DECLINED, "marie@acme.com"));
        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    void updateCandidateStatus_createsApplicationFromAppliedCv() {
        StudentCv cv = cvWithStudent(5L);
        jobOffer.setAppliedCvs(new ArrayList<>(List.of(cv)));
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.empty());
        when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(i -> i.getArgument(0));

        CandidateDto result = service.updateCandidateStatus(1L, 5L, Status.DECLINED, "marie@acme.com");

        assertEquals(Status.DECLINED, result.status());
        ArgumentCaptor<JobApplication> captor = ArgumentCaptor.forClass(JobApplication.class);
        verify(jobApplicationRepository).save(captor.capture());
        assertSame(jobOffer, captor.getValue().getJobOffer());
        assertSame(cv, captor.getValue().getStudentCv());
    }

    @Test
    void updateCandidateStatus_cvNotAppliedToOffer_throwsNotFound() {
        jobOffer.setAppliedCvs(new ArrayList<>());
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> service.updateCandidateStatus(1L, 5L, Status.ACCEPTED, "marie@acme.com"));
    }

    @Test
    void getCandidateCv_allowedViaAppliedCvs() {
        StudentCv cv = new StudentCv();
        cv.setContent(new byte[]{1, 2});
        when(jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(5L, "marie@acme.com")).thenReturn(false);
        when(jobOfferRepository.existsByEmployer_Credentials_EmailAndAppliedCvs_Id("marie@acme.com", 5L)).thenReturn(true);
        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));

        assertArrayEquals(new byte[]{1, 2}, service.getCandidateCv(5L, "marie@acme.com"));
    }

    @Test
    void getCandidateCv_cvMissing_throwsInvalidCv() {
        when(jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(5L, "marie@acme.com")).thenReturn(true);
        when(studentCvRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(InvalidCvException.class,
                () -> service.getCandidateCv(5L, "marie@acme.com"));
    }
}
