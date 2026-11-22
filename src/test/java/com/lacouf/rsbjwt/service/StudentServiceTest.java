package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.ApplicationStateException;
import com.lacouf.rsbjwt.mapper.InterviewMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.InterviewRepository;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private JobOfferMapper jobOfferMapper;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private InterviewMapper interviewMapper;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private JobOffer jobOffer;
    private JobOfferDetailDTO jobOfferDetailDTO;
    private static final String STUDENT_EMAIL = "student@test.com";

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L).firstName("Jean").lastName("Dupont")
                .email(STUDENT_EMAIL).password("pass")
                .matricule("1234567")
                .programe(Programe.TECHNIQUES_INFORMATIQUE)
                .build();

        jobOffer = new JobOffer();
        jobOffer.setId(1L);
        jobOffer.setTitle("Dev Java");

        AdresseDTO adresse = AdresseDTO.builder()
                .pays("Canada").ville("Montreal").rue("Rue Principale")
                .numeroCivic("123").codePostal("H1A1A1")
                .build();

        jobOfferDetailDTO = new JobOfferDetailDTO(
                1L, "Dev Java", "Description", "Java",
                adresse, "20$/h", Status.ACCEPTED,
                LocalDate.now(), LocalDate.of(2025, 9, 1),
                16, Programe.TECHNIQUES_INFORMATIQUE, "Acme Corp"
        );
    }
    private JobApplication application(JobOffer offer, Status status) {
        JobApplication application = new JobApplication();
        application.setJobOffer(offer);
        application.setStatus(status);
        return application;
    }

    private JobOffer offerWithId(Long id) {
        JobOffer offer = new JobOffer();
        offer.setId(id);
        return offer;
    }

    private JobOfferDetailDTO dtoFor(Long id) {
        return new JobOfferDetailDTO(id, "Titre", "Description", "Prerequis", null, "20$/h",
                Status.ACCEPTED, LocalDate.now(), LocalDate.of(2025, 9, 1),
                16, Programe.TECHNIQUES_INFORMATIQUE, "Acme Corp");
    }

    @Test
    @DisplayName("getAcceptedJobOffers retourne les offres du programme de l'étudiant")
    void getAcceptedJobOffers_returnsOffers() {
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(jobOfferRepository.findAllByStatusInAndPrograme(List.of(Status.ACCEPTED, Status.FILLED), Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(jobOffer));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(jobOfferDetailDTO);

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertEquals(1, result.size());
        assertEquals("Dev Java", result.get(0).getTitle());
        verify(studentRepository).findByCredentialsEmail(STUDENT_EMAIL);
        verify(jobOfferRepository).findAllByStatusInAndPrograme(List.of(Status.ACCEPTED, Status.FILLED), Programe.TECHNIQUES_INFORMATIQUE);
    }

    @Test
    @DisplayName("getAcceptedJobOffers retourne les offres même si l'étudiant n'a pas de CV validé")
    void getAcceptedJobOffers_returnsOffers_whenNoCvAccepted() {
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(jobOfferRepository.findAllByStatusInAndPrograme(List.of(Status.ACCEPTED, Status.FILLED), Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(jobOffer));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(jobOfferDetailDTO);

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isApplied());
    }

    @Test
    @DisplayName("getAcceptedJobOffers retourne liste vide si aucune offre pour le programme")
    void getAcceptedJobOffers_returnsEmpty_whenNoOffersForProgram() {
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(jobOfferRepository.findAllByStatusInAndPrograme(List.of(Status.ACCEPTED, Status.FILLED), Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of());

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertTrue(result.isEmpty());
        verifyNoInteractions(jobOfferMapper);
    }

    @Test
    @DisplayName("getAcceptedJobOffers lance 404 quand l'étudiant n'existe pas")
    void getAcceptedJobOffers_throws404_whenStudentNotFound() {
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> studentService.getAcceptedJobOffers(STUDENT_EMAIL));

        verifyNoInteractions(jobOfferRepository);
    }


    @ParameterizedTest(name = "une candidature {0} est transmise dans applicationStatus")
    @EnumSource(Status.class)
    @DisplayName("getAcceptedJobOffers : transmet le statut de la candidature")
    void getAcceptedJobOffers_exposesApplicationStatus(Status applicationStatus) {
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL)).thenReturn(Optional.of(student));
        when(jobApplicationRepository.findByStudentIdWithOffer(1L))
                .thenReturn(List.of(application(jobOffer, applicationStatus)));
        when(jobOfferRepository.findAllByStatusInAndPrograme(List.of(Status.ACCEPTED, Status.FILLED), Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(jobOffer));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(jobOfferDetailDTO);

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertTrue(result.get(0).isApplied());
        assertEquals(applicationStatus, result.get(0).getApplicationStatus());
    }

    @Test
    @DisplayName("getAcceptedJobOffers : garde la candidature la plus récente quand il y en a plusieurs sur la même offre")
    void getAcceptedJobOffers_keepsMostRecentApplication_whenDuplicates() {
        JobOffer offer = offerWithId(10L);
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL)).thenReturn(Optional.of(student));
        when(jobApplicationRepository.findByStudentIdWithOffer(1L)).thenReturn(List.of(
                application(offer, Status.DECLINED),
                application(offer, Status.PENDING)));
        when(jobOfferRepository.findAllByStatusInAndPrograme(List.of(Status.ACCEPTED, Status.FILLED), Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(offer));
        when(jobOfferMapper.toDto(offer)).thenReturn(dtoFor(10L));

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertEquals(1, result.size());
        assertEquals(Status.DECLINED, result.get(0).getApplicationStatus());
    }

    // ── Interview tests ──────────────────────────────────────────────────────

    private InterviewDto stubInterviewDto(Long id, Status status) {
        return new InterviewDto(id, 10L, "Acme Corp", "Dev Java", InterviewType.ONLINE,
                "https://zoom.us", null, null, Instant.now(), List.of(), status, null, false);
    }

    @Test
    @DisplayName("getInterviews retourne les entrevues de l'étudiant")
    void getInterviews_returnsMappedList() {
        Interview interview = new Interview();
        InterviewDto dto = stubInterviewDto(1L, Status.PENDING);
        when(interviewRepository.findAllByStudentEmail(STUDENT_EMAIL)).thenReturn(List.of(interview));
        when(interviewMapper.toInterviewDto(interview)).thenReturn(dto);

        List<InterviewDto> result = studentService.getInterviews(STUDENT_EMAIL);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(interviewRepository).findAllByStudentEmail(STUDENT_EMAIL);
    }

    @Test
    @DisplayName("getInterviews retourne liste vide si aucune entrevue")
    void getInterviews_returnsEmpty() {
        when(interviewRepository.findAllByStudentEmail(STUDENT_EMAIL)).thenReturn(List.of());

        List<InterviewDto> result = studentService.getInterviews(STUDENT_EMAIL);

        assertTrue(result.isEmpty());
        verifyNoInteractions(interviewMapper);
    }

    @Test
    @DisplayName("withExpired: expired=true quand PENDING et tous les créneaux dans le passé")
    void withExpired_true_whenAllSlotsPast() {
        Interview interview = new Interview();
        interview.setStatus(Status.PENDING);
        InterviewSlot past = new InterviewSlot();
        past.setDate(LocalDate.now().minusDays(1));
        interview.setSlots(List.of(past));

        when(interviewRepository.findAllByStudentEmail(STUDENT_EMAIL)).thenReturn(List.of(interview));
        when(interviewMapper.toInterviewDto(interview)).thenReturn(stubInterviewDto(1L, Status.PENDING));

        List<InterviewDto> result = studentService.getInterviews(STUDENT_EMAIL);

        assertTrue(result.get(0).expired());
    }

    @Test
    @DisplayName("withExpired: expired=false quand ACCEPTED même avec créneaux passés")
    void withExpired_false_whenNotPending() {
        Interview interview = new Interview();
        interview.setStatus(Status.ACCEPTED);
        InterviewSlot past = new InterviewSlot();
        past.setDate(LocalDate.now().minusDays(1));
        interview.setSlots(List.of(past));

        when(interviewRepository.findAllByStudentEmail(STUDENT_EMAIL)).thenReturn(List.of(interview));
        when(interviewMapper.toInterviewDto(interview)).thenReturn(stubInterviewDto(1L, Status.ACCEPTED));

        List<InterviewDto> result = studentService.getInterviews(STUDENT_EMAIL);

        assertFalse(result.get(0).expired());
    }

    @Test
    @DisplayName("withExpired: expired=false quand PENDING avec un créneau futur")
    void withExpired_false_whenFutureSlotExists() {
        Interview interview = new Interview();
        interview.setStatus(Status.PENDING);
        InterviewSlot future = new InterviewSlot();
        future.setDate(LocalDate.now().plusDays(1));
        interview.setSlots(List.of(future));

        when(interviewRepository.findAllByStudentEmail(STUDENT_EMAIL)).thenReturn(List.of(interview));
        when(interviewMapper.toInterviewDto(interview)).thenReturn(stubInterviewDto(1L, Status.PENDING));

        List<InterviewDto> result = studentService.getInterviews(STUDENT_EMAIL);

        assertFalse(result.get(0).expired());
    }

    @Test
    @DisplayName("respondToInterview accepte l'entrevue et met à jour le statut")
    void respondToInterview_accepts() {
        Interview interview = new Interview();
        interview.setId(5L);
        interview.setStatus(Status.PENDING);
        interview.setSlots(List.of());

        when(interviewRepository.findByIdAndStudentEmail(5L, STUDENT_EMAIL)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(interview)).thenReturn(interview);
        when(interviewMapper.toInterviewDto(interview)).thenReturn(stubInterviewDto(5L, Status.ACCEPTED));

        InterviewDto result = studentService.respondToInterview(5L,
                new StudentInterviewResponseDto(true, "Je serai présent"), STUDENT_EMAIL);

        assertEquals(Status.ACCEPTED, interview.getStatus());
        assertEquals("Je serai présent", interview.getStudentMessage());
        assertEquals(5L, result.id());
        verify(interviewRepository).save(interview);
    }

    @Test
    @DisplayName("respondToInterview décline l'entrevue et met à jour le statut")
    void respondToInterview_declines() {
        Interview interview = new Interview();
        interview.setId(5L);
        interview.setStatus(Status.PENDING);
        interview.setSlots(List.of());

        when(interviewRepository.findByIdAndStudentEmail(5L, STUDENT_EMAIL)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(interview)).thenReturn(interview);
        when(interviewMapper.toInterviewDto(interview)).thenReturn(stubInterviewDto(5L, Status.DECLINED));

        studentService.respondToInterview(5L,
                new StudentInterviewResponseDto(false, "Pas disponible"), STUDENT_EMAIL);

        assertEquals(Status.DECLINED, interview.getStatus());
        assertEquals("Pas disponible", interview.getStudentMessage());
    }

    @Test
    @DisplayName("respondToInterview lance 404 si entrevue non trouvée")
    void respondToInterview_throwsNotFound() {
        when(interviewRepository.findByIdAndStudentEmail(99L, STUDENT_EMAIL)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> studentService.respondToInterview(99L,
                        new StudentInterviewResponseDto(true, null), STUDENT_EMAIL));
        verify(interviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("respondToInterview lance 409 si l'entrevue n'est plus PENDING")
    void respondToInterview_throwsConflict_whenNotPending() {
        Interview interview = new Interview();
        interview.setId(5L);
        interview.setStatus(Status.ACCEPTED);

        when(interviewRepository.findByIdAndStudentEmail(5L, STUDENT_EMAIL)).thenReturn(Optional.of(interview));

        assertThrows(ApplicationStateException.class,
                () -> studentService.respondToInterview(5L,
                        new StudentInterviewResponseDto(true, null), STUDENT_EMAIL));
        verify(interviewRepository, never()).save(any());
    }
}

