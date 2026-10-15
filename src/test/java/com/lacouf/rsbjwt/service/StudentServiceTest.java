package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @Test
    @DisplayName("getAcceptedJobOffers retourne les offres quand le CV est validé")
    void getAcceptedJobOffers_returnsOffers_whenCvAccepted() {
        when(studentCvRepository.existsByStudentCredentialsEmailAndStatus(STUDENT_EMAIL, Status.ACCEPTED))
                .thenReturn(true);
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(jobOffer));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(jobOfferDetailDTO);

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertEquals(1, result.size());
        assertEquals("Dev Java", result.get(0).title());
        verify(studentCvRepository).existsByStudentCredentialsEmailAndStatus(STUDENT_EMAIL, Status.ACCEPTED);
        verify(jobOfferRepository).findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE);

    }

    @Test
    @DisplayName("getAcceptedJobOffers lance 403 quand aucun CV validé")
    void getAcceptedJobOffers_throwsForbidden_whenNoCvAccepted() {
        when(studentCvRepository.existsByStudentCredentialsEmailAndStatus(STUDENT_EMAIL, Status.ACCEPTED))
                .thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> studentService.getAcceptedJobOffers(STUDENT_EMAIL));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verifyNoInteractions(jobOfferRepository);
        verifyNoInteractions(studentRepository);
    }

    @Test
    @DisplayName("getAcceptedJobOffers retourne liste vide si aucune offre pour le programme")
    void getAcceptedJobOffers_returnsEmpty_whenNoOffersForProgram() {
        when(studentCvRepository.existsByStudentCredentialsEmailAndStatus(STUDENT_EMAIL, Status.ACCEPTED))
                .thenReturn(true);
        when(studentRepository.findByCredentialsEmail(STUDENT_EMAIL))
                .thenReturn(Optional.of(student));
        when(jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of());

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers(STUDENT_EMAIL);

        assertTrue(result.isEmpty());
        verifyNoInteractions(jobOfferMapper);
    }
}
