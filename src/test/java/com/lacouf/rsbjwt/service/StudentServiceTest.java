package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.security.exception.NoValidatedCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
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
    private StudentCvRepository studentCvRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private JobOfferMapper jobOfferMapper;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private JobOffer jobOffer;
    private JobOfferDetailDTO jobOfferDetailDTO;
    private StudentCv latestCv;
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

        latestCv = mock(StudentCv.class);
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
    @DisplayName("getAcceptedJobOffers retourne les offres quand le CV est validé")
    void getAcceptedJobOffers_returnsOffers_whenCvAccepted() {
        when(latestCv.getStatus()).thenReturn(Status.ACCEPTED);
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(studentCvRepository.findFirstByStudentCredentialsEmailOrderByUploadedAtDesc(STUDENT_EMAIL))
                .thenReturn(Optional.of(latestCv));
        when(jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(jobOffer));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(jobOfferDetailDTO);

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertEquals(1, result.size());
        assertEquals("Dev Java", result.get(0).title());
        verify(studentRepository).findByCredentialsEmail(STUDENT_EMAIL);
        verify(studentCvRepository).findFirstByStudentCredentialsEmailOrderByUploadedAtDesc(STUDENT_EMAIL);
        verify(jobOfferRepository).findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE);
    }

    @Test
    @DisplayName("getAcceptedJobOffers lance 403 quand aucun CV validé")
    void getAcceptedJobOffers_throwsForbidden_whenNoCvAccepted() {
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(studentCvRepository.findFirstByStudentCredentialsEmailOrderByUploadedAtDesc(STUDENT_EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(NoValidatedCvException.class,
                () -> studentService.getAcceptedJobOffers(STUDENT_EMAIL));

        verifyNoInteractions(jobOfferRepository);
    }

    @Test
    @DisplayName("getAcceptedJobOffers retourne liste vide si aucune offre pour le programme")
    void getAcceptedJobOffers_returnsEmpty_whenNoOffersForProgram() {
        when(latestCv.getStatus()).thenReturn(Status.ACCEPTED);
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(studentCvRepository.findFirstByStudentCredentialsEmailOrderByUploadedAtDesc(STUDENT_EMAIL))
                .thenReturn(Optional.of(latestCv));
        when(jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE))
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

        verifyNoInteractions(studentCvRepository);
        verifyNoInteractions(jobOfferRepository);
    }

    @ParameterizedTest(name = "un étudiant dont le dernier CV est {0} ne peut pas voir les offres")
    @EnumSource(value = Status.class, names = "ACCEPTED", mode = EnumSource.Mode.EXCLUDE)
    void getAcceptedJobOffers_throwsNoValidatedCv_whenLatestCvNotAccepted(Status status) {
        when(latestCv.getStatus()).thenReturn(status);
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(studentCvRepository.findFirstByStudentCredentialsEmailOrderByUploadedAtDesc(STUDENT_EMAIL))
                .thenReturn(Optional.of(latestCv));

        assertThrows(NoValidatedCvException.class,
                () -> studentService.getAcceptedJobOffers(STUDENT_EMAIL));

        verifyNoInteractions(jobOfferRepository);
    }

    @ParameterizedTest(name = "une candidature {0} est transmise dans applicationStatus")
    @EnumSource(Status.class)
    @DisplayName("getAcceptedJobOffers : transmet le statut de la candidature")
    void getAcceptedJobOffers_exposesApplicationStatus(Status applicationStatus) {
        when(latestCv.getStatus()).thenReturn(Status.ACCEPTED);
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL)).thenReturn(Optional.of(student));
        when(studentCvRepository.findFirstByStudentCredentialsEmailOrderByUploadedAtDesc(STUDENT_EMAIL))
                .thenReturn(Optional.of(latestCv));
        when(jobApplicationRepository.findByStudentIdWithOffer(1L))
                .thenReturn(List.of(application(jobOffer, applicationStatus)));
        when(jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(jobOffer));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(jobOfferDetailDTO);

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertTrue(result.get(0).applied());
        assertEquals(applicationStatus, result.get(0).applicationStatus());
    }

    @Test
    @DisplayName("getAcceptedJobOffers : garde la candidature la plus récente quand il y en a plusieurs sur la même offre")
    void getAcceptedJobOffers_keepsMostRecentApplication_whenDuplicates() {
        JobOffer offer = offerWithId(10L);
        when(latestCv.getStatus()).thenReturn(Status.ACCEPTED);
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL)).thenReturn(Optional.of(student));
        when(studentCvRepository.findFirstByStudentCredentialsEmailOrderByUploadedAtDesc(STUDENT_EMAIL))
                .thenReturn(Optional.of(latestCv));
        when(jobApplicationRepository.findByStudentIdWithOffer(1L)).thenReturn(List.of(
                application(offer, Status.DECLINED),
                application(offer, Status.PENDING)));
        when(jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(offer));
        when(jobOfferMapper.toDto(offer)).thenReturn(dtoFor(10L));

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertEquals(1, result.size());
        assertEquals(Status.DECLINED, result.get(0).applicationStatus());
    }
}

