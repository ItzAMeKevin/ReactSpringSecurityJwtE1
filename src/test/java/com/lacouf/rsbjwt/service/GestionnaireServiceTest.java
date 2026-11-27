package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.CvMapper;
import com.lacouf.rsbjwt.mapper.CandidatureMapper;
import com.lacouf.rsbjwt.mapper.ContractMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferNotFoundException;
import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import com.lacouf.rsbjwt.service.dto.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

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
    @Mock private CandidatureMapper candidatureMapper;
    @Mock private ContractRepository contractRepository;
    @Mock private ContractMapper contractMapper;
    @Mock private StudentRepository studentRepository;
    @Mock private EmployerRepository employerRepository;
    @Mock private ManagerRepository managerRepository;
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
    void createsAndPersistsGeneratedContractPdf() throws Exception {
        Student student = new Student();
        student.setFirstName("Alex");
        student.setLastName("Student");
        Employer employer = new Employer();
        employer.setCompanyName("Example Company");
        Manager manager = new Manager();
        manager.setFirstName("Morgan");
        manager.setLastName("Manager");
        JobOffer offer = validOffer();
        offer.setEmployer(employer);

        when(jobApplicationRepository.findById(5L)).thenReturn(Optional.of(makeJobApplication(1L, 2L, 4L)));
        when(jobOfferRepository.findById(4L)).thenReturn(Optional.of(offer));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(employerRepository.findById(2L)).thenReturn(Optional.of(employer));
        when(managerRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ContractDto expectedDto = ContractDto.builder().id(10L).build();
        when(contractMapper.toDto(any(Contract.class))).thenReturn(expectedDto);

        ContractDto result = service.createContract(new CandidatureDto(5L, null, null, null, null, null), 3L);

        assertSame(expectedDto, result);
        org.mockito.ArgumentCaptor<Contract> contractCaptor = org.mockito.ArgumentCaptor.forClass(Contract.class);
        verify(contractRepository).save(contractCaptor.capture());
        Contract savedContract = contractCaptor.getValue();
        assertEquals("contrat-stage-1.pdf", savedContract.getFileName());
        assertEquals("application/pdf", savedContract.getContentType());
        assertEquals(Status.UNSIGNED, savedContract.getStatus());
        assertEquals(savedContract.getContent().length, savedContract.getSize());
        assertSame(student, savedContract.getStudent());
        assertSame(employer, savedContract.getEmployer());
        assertSame(manager, savedContract.getManager());
        assertFalse(savedContract.isSignedByStudent());
        assertFalse(savedContract.isSignedByEmployer());
        assertFalse(savedContract.isSignedByManager());
        assertSame(savedContract, student.getContract());
        assertSame(savedContract, employer.getContract());
        assertSame(savedContract, manager.getContract());

        try (var document = Loader.loadPDF(savedContract.getContent())) {
            String pdfText = new PDFTextStripper().getText(document);
            assertEquals(3, document.getNumberOfPages());
            assertTrue(pdfText.contains("Alex Student"));
            assertTrue(pdfText.contains("Example Company"));
            assertTrue(pdfText.contains("04/05/2026"));
        }
        verify(studentRepository).save(student);
        verify(employerRepository).save(employer);
        verify(managerRepository).save(manager);
    }

    @Test
    void createContractRejectsMissingApplicationOrRequiredParty() {
        CandidatureDto dto = new CandidatureDto(5L, null, null, null, null, null);

        when(jobApplicationRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ApplicationNotFoundException.class, () -> service.createContract(dto, 3L));

        when(jobApplicationRepository.findById(5L)).thenReturn(Optional.of(makeJobApplication(1L, 2L, 4L)));
        when(jobOfferRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(JobOfferNotFoundException.class, () -> service.createContract(dto, 3L));

        when(jobOfferRepository.findById(4L)).thenReturn(Optional.of(validOffer()));
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.createContract(dto, 3L));

        verifyNoInteractions(contractRepository, employerRepository, managerRepository);
    }

    @Test
    void createContractDoesNotPersistWhenOfferDataCannotRender() {
        JobOffer offer = validOffer();
        offer.setSalary(null);
        Employer employer = new Employer();
        employer.setCompanyName("Example Company");
        offer.setEmployer(employer);
        when(jobApplicationRepository.findById(5L)).thenReturn(Optional.of(makeJobApplication(1L, 2L, 4L)));
        when(jobOfferRepository.findById(4L)).thenReturn(Optional.of(offer));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(employerRepository.findById(2L)).thenReturn(Optional.of(employer));
        when(managerRepository.findById(3L)).thenReturn(Optional.of(new Manager()));

        assertThrows(IllegalArgumentException.class, () ->
                service.createContract(new CandidatureDto(5L, null, null, null, null, null), 3L));
        verifyNoInteractions(contractRepository);
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

        CandidatureDto dto = new CandidatureDto(
                1L,
                new CandidatureDto.StudentInfoDto("Jane", "Doe", "1234567", "jane@test.com"),
                new CandidatureDto.JobOfferInfoDto("Developer", "Acme", 12),
                Status.PENDING,
                null,
                null);
        when(jobApplicationRepository.findAll()).thenReturn(List.of(application));
        when(candidatureMapper.toDto(application)).thenReturn(dto);
        List<CandidatureDto> result = service.getAllCandidatures();
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
        assertEquals(1L, dto.id());
        assertEquals("Jane", dto.student().firstName());
        assertEquals("jane@test.com", dto.student().email());
        assertEquals("Acme", dto.jobOffer().companyName());
        assertEquals(12, dto.jobOffer().durationInWeeks());
        assertEquals(Status.PENDING, dto.status());

        when(jobApplicationRepository.findAll()).thenReturn(List.of());
        assertTrue(service.getAllCandidatures().isEmpty());
    }

    private JobApplication makeJobApplication(long studentId, long employerId, long offerId) {
        Student student = new Student();
        ReflectionTestUtils.setField(student, "id", studentId);
        Employer employer = new Employer();
        ReflectionTestUtils.setField(employer, "id", employerId);
        JobOffer offer = new JobOffer();
        ReflectionTestUtils.setField(offer, "id", offerId);
        offer.setEmployer(employer);
        JobApplication application = new JobApplication();
        application.setStudent(student);
        application.setJobOffer(offer);
        return application;
    }

    private JobOffer validOffer() {
        JobOffer offer = new JobOffer();
        offer.setTitle("Software Developer Intern");
        offer.setDescription("Build and test web applications.");
        offer.setSalary("$22/hour");
        offer.setStartingDate(LocalDate.of(2026, 5, 4));
        offer.setDurationInWeeks(12);
        offer.setAdresse(Adresse.builder()
                .numeroCivic("123")
                .rue("Main Street")
                .ville("Montreal")
                .codePostal("H1A 1A1")
                .pays("Canada")
                .build());
        return offer;
    }
}
