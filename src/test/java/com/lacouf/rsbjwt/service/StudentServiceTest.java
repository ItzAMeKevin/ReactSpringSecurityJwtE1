package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.security.exception.NoValidatedCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private JobOfferRepository jobOfferRepository;
    @Mock private StudentCvRepository studentCvRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private JobOfferMapper jobOfferMapper;
    @Mock private JobApplicationRepository jobApplicationRepository;
    @InjectMocks private StudentService studentService;

    private Student studentWithPrograme() {
        Student student = new Student();
        student.setId(1L);
        student.setPrograme(Programe.TECHNIQUES_INFORMATIQUE);
        return student;
    }

    @Test
    void rejectsUnknownStudent() {
        when(studentRepository.findByCredentialsEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> studentService.getAcceptedJobOffers("missing@test.com"));
        verifyNoInteractions(studentCvRepository, jobOfferRepository, jobOfferMapper);
    }

    @Test
    void rejectsStudentWithNoCv() {
        Student student = studentWithPrograme();
        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentCvRepository.existsByStudentAndStatus(student, Status.ACCEPTED)).thenReturn(false);

        assertThrows(NoValidatedCvException.class,
                () -> studentService.getAcceptedJobOffers("student@test.com"));
        verifyNoInteractions(jobOfferRepository, jobOfferMapper);
    }

    @Test
    void returnsOffersWithNoApplicationAnnotation() {
        Student student = studentWithPrograme();
        JobOffer offer = new JobOffer();
        offer.setId(10L);
        JobOfferDetailDTO dto = new JobOfferDetailDTO(10L, "Java Dev", "Desc", "Prereq", null,
                "20$/h", Status.ACCEPTED, LocalDate.now(), LocalDate.of(2026, 9, 1),
                16, Programe.TECHNIQUES_INFORMATIQUE, "ACME");

        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentCvRepository.existsByStudentAndStatus(student, Status.ACCEPTED)).thenReturn(true);
        when(jobApplicationRepository.findByStudentIdWithOffer(1L)).thenReturn(List.of());
        when(jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(offer));
        when(jobOfferMapper.toDto(offer)).thenReturn(dto);

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers("student@test.com");

        assertEquals(1, result.size());
        assertFalse(result.get(0).applied());
    }

    @Test
    void annotatesOfferWhenStudentHasApplied() {
        Student student = studentWithPrograme();
        JobOffer offer = new JobOffer();
        offer.setId(10L);
        JobOfferDetailDTO dto = new JobOfferDetailDTO(10L, "Java Dev", "Desc", "Prereq", null,
                "20$/h", Status.ACCEPTED, LocalDate.now(), LocalDate.of(2026, 9, 1),
                16, Programe.TECHNIQUES_INFORMATIQUE, "ACME");

        JobApplication application = new JobApplication();
        application.setJobOffer(offer);
        application.setStatus(Status.PENDING);

        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentCvRepository.existsByStudentAndStatus(student, Status.ACCEPTED)).thenReturn(true);
        when(jobApplicationRepository.findByStudentIdWithOffer(1L)).thenReturn(List.of(application));
        when(jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, Programe.TECHNIQUES_INFORMATIQUE))
                .thenReturn(List.of(offer));
        when(jobOfferMapper.toDto(offer)).thenReturn(dto);

        List<JobOfferDetailDTO> result = studentService.getAcceptedJobOffers("student@test.com");

        assertEquals(1, result.size());
        assertTrue(result.get(0).applied());
        assertEquals(Status.PENDING, result.get(0).applicationStatus());
    }
}
