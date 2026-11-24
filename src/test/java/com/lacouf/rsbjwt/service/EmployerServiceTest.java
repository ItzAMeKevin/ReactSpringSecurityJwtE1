package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.CandidateMapper;
import com.lacouf.rsbjwt.mapper.CandidatureMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferUpdateException;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
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

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private JobOfferMapper jobOfferMapper;

    @Mock
    private SystemeService systemeService;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private StudentCvRepository studentCvRepository;

    @Spy
    private CandidateMapper candidateMapper = Mappers.getMapper(CandidateMapper.class);

    @Mock
    private CandidatureMapper candidatureMapper;

    @InjectMocks
    private EmployerService employerService;

    private Employer employer;
    private JobOffer jobOffer;
    private JobOfferDetailDTO jobOfferDetailDTO;
    private AdresseDTO adresse;

    @BeforeEach
    void setUp() {
        employer = new Employer();
        employer.setId(42L);
        employer.setCompanyName("ACME Inc.");

        jobOffer = new JobOffer();
        jobOffer.setId(1L);
        jobOffer.setTitle("Développeur Java");

        adresse = AdresseDTO.builder()
                .pays("Canada").ville("Montréal").rue("Ste-Catherine")
                .numeroCivic("100").codePostal("H2X 1Y5")
                .build();

        jobOfferDetailDTO = new JobOfferDetailDTO(
                1L, "Développeur Java", "Desc", "Preq", adresse,
                "60000$", Status.PENDING, LocalDate.now(),
                LocalDate.of(2025, 3, 1), 12, Programe.TECHNIQUES_INFORMATIQUE, "ACME Inc.");
    }

    private JobOfferCreateDTO validCreateDto() {
        return new JobOfferCreateDTO(
                "Titre", "Desc", "Preq", adresse, "60000$",
                LocalDate.of(2025, 3, 1), 12, Programe.TECHNIQUES_INFORMATIQUE, 3);
    }


    //               getJobOffres

    @Test
    @DisplayName("getJobOffres retourne la liste mappée")
    void getJobOffres_returnsMappedList() {
        // Arrange
        String email = "marie@acme.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferRepository.getJobOffersByEmployerId(employer.getId()))
                .thenReturn(List.of(jobOffer, jobOffer));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(jobOfferDetailDTO);
        when(jobApplicationRepository.countByOfferIdsAndStatus(any(), eq(Status.AWAITING_CONTRACT)))
                .thenReturn(List.of());

        // Act
        List<JobOfferDetailDTO> result = employerService.getJobOffres(email);

        // Assert
        assertEquals(2, result.size());
        verify(employerRepository).findByCredentialsEmail(email);
        verify(jobOfferRepository).getJobOffersByEmployerId(employer.getId());
        verify(jobOfferMapper, times(2)).toDto(jobOffer);
    }

    @Test
    @DisplayName("getJobOffres liste vide si aucune offre")
    void getJobOffres_returnsEmptyList() {
        // Arrange
        String email = "vide@test.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferRepository.getJobOffersByEmployerId(employer.getId())).thenReturn(List.of());

        // Act
        List<JobOfferDetailDTO> result = employerService.getJobOffres(email);

        // Assert
        assertTrue(result.isEmpty());
        verify(employerRepository).findByCredentialsEmail(email);
        verify(jobOfferRepository).getJobOffersByEmployerId(employer.getId());
        verifyNoInteractions(jobOfferMapper);
    }

    //              addJobOffer

    @Test
    @DisplayName("addJobOffer retourne le DTO créé et lie l'employeur")
    void addJobOffer_returnsCreatedDto() {
        // Arrange
        JobOfferCreateDTO dto = new JobOfferCreateDTO(
                "Titre", "Desc", "Preq",
                adresse,
                "60000$",
                LocalDate.of(2025, 3, 1),
                12,
                Programe.TECHNIQUES_INFORMATIQUE,
                3
        );
        JobOfferDetailDTO expected = new JobOfferDetailDTO(
                1L, "Titre", "Desc", "Preq",
                adresse,
                "60000$", Status.PENDING,
                LocalDate.now(), LocalDate.of(2025, 3, 1),
                12, Programe.TECHNIQUES_INFORMATIQUE, "ACME Inc."
        );

        String email = "marie@acme.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferMapper.toEntity(dto)).thenReturn(jobOffer);
        when(jobOfferRepository.save(jobOffer)).thenReturn(jobOffer);
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(expected);

        // Act
        JobOfferDetailDTO result = employerService.addJobOffer(dto, email);

        // Assert
        assertEquals(expected, result);
        verify(employerRepository).findByCredentialsEmail(email);
        verify(jobOfferMapper).toEntity(dto);
        assertEquals(employer, jobOffer.getEmployer());
        verify(jobOfferRepository).save(jobOffer);
        verify(jobOfferMapper).toDto(jobOffer);
    }

    @Test
    @DisplayName("addJobOffer lance EmployerNotFoundException si employeur inexistant")
    void addJobOffer_throwsWhenEmployerNotFound() {
        // Arrange
        JobOfferCreateDTO dto = new JobOfferCreateDTO(
                "Titre", "Desc", "Preq",
                adresse,
                "60000$",
                LocalDate.of(2025, 3, 1),
                12,
                Programe.TECHNIQUES_INFORMATIQUE,
                3
        );
        String email = "inconnu@test.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EmployerNotFoundException.class,
                () -> employerService.addJobOffer(dto, email));

        verify(jobOfferRepository, never()).save(any());
        verify(jobOfferMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("addJobOffer notifie les gestionnaires de la nouvelle offre")
    void addJobOffer_notifiesManagers() {
        String email = "marie@acme.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferMapper.toEntity(any())).thenReturn(jobOffer);
        when(jobOfferRepository.save(jobOffer)).thenReturn(jobOffer);

        employerService.addJobOffer(validCreateDto(), email);

        verify(systemeService).notifyManagers(NotificationType.JOB_OFFER, 1L, "notification.types.JOB_OFFER_CREATED.message");
    }

    @Test
    @DisplayName("addJobOffer ne notifie personne si l'employeur est introuvable")
    void addJobOffer_employerNotFound_noNotification() {
        when(employerRepository.findByCredentialsEmail("x@test.com")).thenReturn(Optional.empty());

        assertThrows(EmployerNotFoundException.class,
                () -> employerService.addJobOffer(validCreateDto(), "x@test.com"));
        verifyNoInteractions(systemeService);
    }

    @Test
    @DisplayName("updateRefusedJobOffer ne modifie qu'une offre refusée et la remet en attente")
    void updateRefusedJobOffer_onlyUpdatesDeclinedOffer() {
        String email = "marie@acme.com";
        JobOffer offer = new JobOffer();
        offer.setId(1L);
        offer.setStatus(Status.DECLINED);
        offer.setRefusalFeedback("Not suitable");
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, email)).thenReturn(Optional.of(offer));
        when(jobOfferMapper.toEntity(any(JobOfferCreateDTO.class))).thenReturn(new JobOffer());
        when(jobOfferRepository.save(offer)).thenReturn(offer);
        when(jobOfferMapper.toDto(offer)).thenReturn(JobOfferDetailDTO.builder().build());

        assertTrue(employerService.updateRefusedJobOffer(1L, validCreateDto(), email).isPresent());
        assertEquals(Status.PENDING, offer.getStatus());
        assertNull(offer.getRefusalFeedback());
        verify(systemeService).notifyManagers(NotificationType.JOB_OFFER, 1L,
                "notification.types.JOB_OFFER_RESUBMITTED.message");

        offer.setStatus(Status.ACCEPTED);
        assertThrows(JobOfferUpdateException.class,
                () -> employerService.updateRefusedJobOffer(1L, validCreateDto(), email));
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(2L, email)).thenReturn(Optional.empty());
        assertTrue(employerService.updateRefusedJobOffer(2L, validCreateDto(), email).isEmpty());
        verifyNoMoreInteractions(systemeService);
    }

    @Test
    @DisplayName("getCandidates retourne une liste vide s'il n'y a aucune candidature")
    void getCandidates_emptyList() {
        String email = "marie@acme.com";
        jobOffer.setAppliedCvs(new ArrayList<>());
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, email)).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdWithStudent(1L)).thenReturn(List.of());

        List<CandidateDto> result = employerService.getCandidates(1L, email);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCandidates refuse une offre qui n'appartient pas à l'employeur")
    void getCandidates_offerNotOwned_throws() {
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "other@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> employerService.getCandidates(1L, "other@test.com"));
    }

    @Test
    @DisplayName("getCandidates mappe nom, courriel, date et statut")
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
        when(candidateMapper.toCandidateDto(application)).thenReturn(
                new CandidateDto(5L, 1L, "Jean", "Dupont", "jean@test.com",
                        Instant.parse("2026-02-01T12:00:00Z"), Status.PENDING));

        List<CandidateDto> result = employerService.getCandidates(1L, email);

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
    @DisplayName("rejectCandidate passe une candidature en attente à DECLINED")
    void rejectCandidate_pendingApplication_setsDeclined() {
        JobApplication application = pendingApplication(cvWithStudent(5L));
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.of(application));
        when(jobApplicationRepository.save(application)).thenReturn(application);

        CandidateDto result = employerService.rejectCandidate(1L, 5L, "marie@acme.com");

        assertEquals(Status.DECLINED, result.status());
        assertEquals(Status.DECLINED, application.getStatus());
    }

    @Test
    @DisplayName("getCandidateCv refuse un CV qui n'appartient pas à l'employeur")
    void getCandidateCv_notOwned_throws() {
        when(jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(5L, "marie@acme.com"))
                .thenReturn(false);
        when(jobOfferRepository.existsByEmployer_Credentials_EmailAndAppliedCvs_Id("marie@acme.com", 5L))
                .thenReturn(false);

        assertThrows(ApplicationNotFoundException.class,
                () -> employerService.getCandidateCv(5L, "marie@acme.com"));
        verify(studentCvRepository, never()).findById(any());
    }

    @Test
    @DisplayName("getCandidateCv retourne le contenu PDF si l'employeur y a accès")
    void getCandidateCv_returnsContent() {
        byte[] pdf = {37, 80, 68, 70};
        StudentCv cv = new StudentCv();
        cv.setContent(pdf);
        when(jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(5L, "marie@acme.com"))
                .thenReturn(true);
        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));

        assertArrayEquals(pdf, employerService.getCandidateCv(5L, "marie@acme.com"));
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
        when(candidateMapper.toCandidateDto(cv, cv.getUploadedAt(), Status.PENDING)).thenReturn(
                new CandidateDto(5L, 1L, "Jean", "Dupont", "jean@test.com",
                        Instant.parse("2026-01-15T10:00:00Z"), Status.PENDING));

        List<CandidateDto> result = employerService.getCandidates(1L, "marie@acme.com");

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
        when(candidateMapper.toCandidateDto(application)).thenReturn(
                new CandidateDto(5L, 1L, "Jean", "Dupont", "jean@test.com", null, Status.ACCEPTED));

        List<CandidateDto> result = employerService.getCandidates(1L, "marie@acme.com");

        assertEquals(1, result.size());
        assertEquals(Status.ACCEPTED, result.getFirst().status());
    }

    @Test
    @DisplayName("rejectCandidate refuse une candidature qui n'est plus ouverte")
    void rejectCandidate_closedApplication_throws() {
        JobApplication application = pendingApplication(cvWithStudent(5L));
        application.setStatus(Status.HIRE_PENDING);
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.of(application));

        InvalidStateTransitionException exception = assertThrows(InvalidStateTransitionException.class,
                () -> employerService.rejectCandidate(1L, 5L, "marie@acme.com"));

        assertEquals("errors.applicationNotOpen", exception.getMessage());
        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("rejectCandidate crée la candidature à partir du CV posé sur l'offre")
    void rejectCandidate_createsApplicationFromAppliedCv() {
        StudentCv cv = cvWithStudent(5L);
        jobOffer.setAppliedCvs(new ArrayList<>(List.of(cv)));
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.empty());
        when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(i -> i.getArgument(0));
        when(candidateMapper.toCandidateDto(any(JobApplication.class))).thenAnswer(i -> {
            JobApplication app = i.getArgument(0);
            return new CandidateDto(app.getStudentCv().getId(), app.getId(), null, null, null, null, app.getStatus());
        });

        CandidateDto result = employerService.rejectCandidate(1L, 5L, "marie@acme.com");

        assertEquals(Status.DECLINED, result.status());
        ArgumentCaptor<JobApplication> captor = ArgumentCaptor.forClass(JobApplication.class);
        verify(jobApplicationRepository).save(captor.capture());
        assertSame(jobOffer, captor.getValue().getJobOffer());
        assertSame(cv, captor.getValue().getStudentCv());
    }

    @Test
    @DisplayName("rejectCandidate lève ApplicationNotFoundException si le CV n'a pas été posé sur l'offre")
    void rejectCandidate_cvNotAppliedToOffer_throwsNotFound() {
        jobOffer.setAppliedCvs(new ArrayList<>());
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> employerService.rejectCandidate(1L, 5L, "marie@acme.com"));
    }

    @Test
    void getCandidateCv_allowedViaAppliedCvs() {
        StudentCv cv = new StudentCv();
        cv.setContent(new byte[]{1, 2});
        when(jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(5L, "marie@acme.com")).thenReturn(false);
        when(jobOfferRepository.existsByEmployer_Credentials_EmailAndAppliedCvs_Id("marie@acme.com", 5L)).thenReturn(true);
        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));

        assertArrayEquals(new byte[]{1, 2}, employerService.getCandidateCv(5L, "marie@acme.com"));
    }

    @Test
    void getCandidateCv_cvMissing_throwsCvException() {
        when(jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(5L, "marie@acme.com")).thenReturn(true);
        when(studentCvRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(CvException.class,
                () -> employerService.getCandidateCv(5L, "marie@acme.com"));
    }

    private JobApplication pendingApplication(StudentCv cv) {
        JobApplication application = new JobApplication();
        application.setJobOffer(jobOffer);
        application.setStudentCv(cv);
        application.setStatus(Status.PENDING);
        return application;
    }

    private void givenHireableOffer(int numberOfPositions, long heldSeats) {
        jobOffer.setStatus(Status.ACCEPTED);
        jobOffer.setNumberOfPositions(numberOfPositions);
        when(jobOfferRepository.findOwnedOfferForUpdate(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));
        when(jobApplicationRepository.countByJobOffer_IdAndStatusIn(1L, JobApplication.SEAT_HOLDING_STATUSES)).thenReturn(heldSeats);
    }

    @Test
    @DisplayName("hireStudent passe le candidat à HIRE_PENDING et notifie l'étudiant")
    void hireStudent_setsHirePendingAndNotifiesStudent() {
        givenHireableOffer(2, 0L);
        StudentCv cv = cvWithStudent(5L);
        JobApplication application = pendingApplication(cv);
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.of(application));
        when(jobApplicationRepository.save(application)).thenReturn(application);

        CandidateDto result = employerService.hireStudent(1L, 5L, "marie@acme.com");

        assertEquals(Status.HIRE_PENDING, result.status());
        assertEquals(Status.HIRE_PENDING, application.getStatus());
        verify(systemeService).notifyUsers(List.of(cv.getStudent()), NotificationType.JOB_OFFER, 1L,
                "notification.types.JOB_OFFER_HIRED.message");
    }

    @Test
    @DisplayName("hireStudent ne change pas le statut de l'offre tant que l'étudiant n'a pas accepté")
    void hireStudent_positionsRemaining_offerStaysAccepted() {
        givenHireableOffer(2, 1L);
        JobApplication application = pendingApplication(cvWithStudent(5L));
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.of(application));
        when(jobApplicationRepository.save(application)).thenReturn(application);

        employerService.hireStudent(1L, 5L, "marie@acme.com");

        assertEquals(Status.ACCEPTED, jobOffer.getStatus());
        verify(jobOfferRepository, never()).save(any());
    }

    @Test
    @DisplayName("hireStudent refuse l'envoi quand tous les postes sont déjà réservés")
    void hireStudent_allPositionsHeld_throwsNoPositionsLeft() {
        givenHireableOffer(2, 2L);

        InvalidStateTransitionException exception = assertThrows(InvalidStateTransitionException.class,
                () -> employerService.hireStudent(1L, 5L, "marie@acme.com"));

        assertEquals("errors.noPositionsLeft", exception.getMessage());
        verify(jobApplicationRepository, never()).save(any());
        verifyNoInteractions(systemeService);
    }

    @Test
    @DisplayName("hireStudent refuse une offre qui n'est pas ACCEPTED")
    void hireStudent_offerNotAccepted_throwsInvalidState() {
        jobOffer.setStatus(Status.FILLED);
        when(jobOfferRepository.findOwnedOfferForUpdate(1L, "marie@acme.com")).thenReturn(Optional.of(jobOffer));

        InvalidStateTransitionException exception = assertThrows(InvalidStateTransitionException.class,
                () -> employerService.hireStudent(1L, 5L, "marie@acme.com"));

        assertEquals("errors.offerNotAccepted", exception.getMessage());
        verify(jobApplicationRepository, never()).save(any());
        verifyNoInteractions(systemeService);
    }

    @Test
    @DisplayName("hireStudent refuse une candidature qui n'est plus ouverte")
    void hireStudent_closedApplication_throws() {
        givenHireableOffer(2, 0L);
        JobApplication application = pendingApplication(cvWithStudent(5L));
        application.setStatus(Status.DECLINED);
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.of(application));

        InvalidStateTransitionException exception = assertThrows(InvalidStateTransitionException.class,
                () -> employerService.hireStudent(1L, 5L, "marie@acme.com"));

        assertEquals("errors.applicationNotOpen", exception.getMessage());
        assertEquals(Status.DECLINED, application.getStatus());
        verify(jobApplicationRepository, never()).save(any());
        verifyNoInteractions(systemeService);
    }

    @Test
    @DisplayName("hireStudent lève ApplicationNotFoundException si l'offre n'appartient pas à l'employeur")
    void hireStudent_offerNotOwned_throwsNotFound() {
        when(jobOfferRepository.findOwnedOfferForUpdate(1L, "marie@acme.com")).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> employerService.hireStudent(1L, 5L, "marie@acme.com"));
        verifyNoInteractions(jobApplicationRepository, systemeService);
    }

    @Test
    @DisplayName("hireStudent lève ApplicationNotFoundException si la candidature n'existe pas")
    void hireStudent_applicationMissing_throwsNotFound() {
        givenHireableOffer(2, 0L);
        when(jobApplicationRepository.findByOfferIdAndCvIdWithStudent(1L, 5L)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> employerService.hireStudent(1L, 5L, "marie@acme.com"));
        verify(jobApplicationRepository, never()).save(any());
        verifyNoInteractions(systemeService);
    }

    @Test
    @DisplayName("getHirePendingCandidates mappe les offres d'embauche en attente de réponse")
    void getHirePendingCandidates_mapsApplications() {
        JobApplication application = pendingApplication(cvWithStudent(5L));
        application.setStatus(Status.HIRE_PENDING);
        CandidatureDto dto = new CandidatureDto(1L, null, null, Status.HIRE_PENDING, null, null);
        when(jobApplicationRepository.findByEmployerEmailAndStatus("marie@acme.com", Status.HIRE_PENDING))
                .thenReturn(List.of(application));
        when(candidatureMapper.toCandidateDto(application)).thenReturn(dto);

        List<CandidatureDto> result = employerService.getHirePendingCandidates("marie@acme.com");

        assertEquals(List.of(dto), result);
    }

    @Test
    @DisplayName("getHirePendingCandidates retourne une liste vide")
    void getHirePendingCandidates_returnsEmptyList() {
        when(jobApplicationRepository.findByEmployerEmailAndStatus("marie@acme.com", Status.HIRE_PENDING))
                .thenReturn(List.of());

        assertTrue(employerService.getHirePendingCandidates("marie@acme.com").isEmpty());
    }
}
