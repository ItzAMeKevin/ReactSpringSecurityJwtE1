package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.mapper.CandidatureMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private CandidatureMapper candidatureMapper;

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

    @Test
    @DisplayName("getHires mappe les candidatures dont l'entretien est terminé")
    void getHires_mapsCompletedInterviews() {
        JobApplication application = new JobApplication();
        application.setId(7L);
        CandidatureDto dto = new CandidatureDto(7L, null, null,
                Status.INTERVIEW_COMPLETED, null, null);

        when(jobApplicationRepository.findByJobOfferEmployerCredentialsEmailAndPendingStatus(
                "employer@test.com", Status.INTERVIEW_COMPLETED))
                .thenReturn(Optional.of(List.of(application)));
        when(candidatureMapper.toCandidateDto(application)).thenReturn(dto);

        List<CandidatureDto> result = studentService.getHires("employer@test.com");

        assertEquals(List.of(dto), result);
        verify(candidatureMapper).toCandidateDto(application);
    }

    @Test
    @DisplayName("getHires retourne une liste vide")
    void getHires_returnsEmptyList() {
        when(jobApplicationRepository.findByJobOfferEmployerCredentialsEmailAndPendingStatus(
                "employer@test.com", Status.INTERVIEW_COMPLETED))
                .thenReturn(Optional.of(List.of()));

        assertTrue(studentService.getHires("employer@test.com").isEmpty());
    }

    private JobApplication interviewCompletedApplication() {
        StudentCv cv = new StudentCv();
        cv.setStudent(student);
        JobApplication application = new JobApplication();
        application.setId(9L);
        application.setStudentCv(cv);
        application.setStatus(Status.INTERVIEW_COMPLETED);
        return application;
    }

    @Test
    @DisplayName("acceptHire passe l'entretien terminé à AWAITING_CONTRACT")
    void acceptHire_changesStatus() {
        JobApplication application = interviewCompletedApplication();
        CandidatureDto dto = new CandidatureDto(9L, null, null,
                Status.AWAITING_CONTRACT, null, null);
        when(jobApplicationRepository.findById(9L)).thenReturn(Optional.of(application));
        when(jobApplicationRepository.save(application)).thenReturn(application);
        when(candidatureMapper.toCandidateDto(application)).thenReturn(dto);

        CandidatureDto result = studentService.acceptHire(9L,
                new CandidateDto(null, null, null, null, null, null, null),
                STUDENT_EMAIL);

        assertEquals(Status.AWAITING_CONTRACT, application.getStatus());
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("declineHire passe l'entretien terminé à DECLINED")
    void declineHire_changesStatus() {
        JobApplication application = interviewCompletedApplication();
        CandidatureDto dto = new CandidatureDto(9L, null, null,
                Status.DECLINED, null, null);
        when(jobApplicationRepository.findById(9L)).thenReturn(Optional.of(application));
        when(jobApplicationRepository.save(application)).thenReturn(application);
        when(candidatureMapper.toCandidateDto(application)).thenReturn(dto);

        CandidatureDto result = studentService.declineHire(9L,
                new CandidateDto(null, null, null, null, null, null, null),
                STUDENT_EMAIL);

        assertEquals(Status.DECLINED, application.getStatus());
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("acceptHire refuse une candidature dont l'étudiant ne possède pas")
    void acceptHire_wrongStudentThrows() {
        JobApplication application = interviewCompletedApplication();
        when(jobApplicationRepository.findById(9L)).thenReturn(Optional.of(application));

        assertThrows(UserNotFoundException.class, () -> studentService.acceptHire(9L,
                new CandidateDto(null, null, null, null, null, null, null),
                "other@test.com"));
        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("declineHire refuse une candidature qui n'a pas terminé l'entretien")
    void declineHire_invalidStateThrows() {
        JobApplication application = interviewCompletedApplication();
        application.setStatus(Status.INTERVIEW_SCHEDULED);
        when(jobApplicationRepository.findById(9L)).thenReturn(Optional.of(application));

        assertThrows(InvalidStateTransitionException.class, () -> studentService.declineHire(9L,
                new CandidateDto(null, null, null, null, null, null, null),
                STUDENT_EMAIL));
        verify(jobApplicationRepository, never()).save(any());
    }
}

