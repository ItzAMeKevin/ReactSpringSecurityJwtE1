package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

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

    @Test
    void returnsAcceptedOffersForStudentWithAcceptedCv() {
        Student student = new Student();
        student.setId(1L);
        JobOffer offer = new JobOffer();
        JobOfferDetailDTO dto = JobOfferDetailDTO.builder().id(2L).title("Java").build();

        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentCvRepository.existsByStudentAndStatus(student, Status.ACCEPTED)).thenReturn(true);
        when(jobOfferRepository.findAcceptedJobOffersByStudentId(1L)).thenReturn(List.of(offer));
        when(jobOfferMapper.toDtoList(List.of(offer))).thenReturn(List.of(dto));

        assertEquals(List.of(dto), studentService.getAcceptedJobOffers("student@test.com"));
    }

    @Test
    void returnsEmptyListWhenNoAcceptedOffers() {
        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentCvRepository.existsByStudentAndStatus(student, Status.ACCEPTED)).thenReturn(true);
        when(jobOfferRepository.findAcceptedJobOffersByStudentId(1L)).thenReturn(List.of());
        when(jobOfferMapper.toDtoList(List.of())).thenReturn(List.of());

        assertTrue(studentService.getAcceptedJobOffers("student@test.com").isEmpty());
    }

    @Test
    void rejectsUnknownStudent() {
        when(studentRepository.findByCredentialsEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> studentService.getAcceptedJobOffers("missing@test.com"));
        verifyNoInteractions(studentCvRepository, jobOfferRepository, jobOfferMapper);
    }

    @Test
    void rejectsStudentWithoutAcceptedCv() {
        Student student = new Student();
        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentCvRepository.existsByStudentAndStatus(student, Status.ACCEPTED)).thenReturn(false);

        assertThrows(NoValidatedCvException.class,
                () -> studentService.getAcceptedJobOffers("student@test.com"));
        verifyNoInteractions(jobOfferRepository, jobOfferMapper);
    }
}
