package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GestionnaireServiceTest {

    @Mock
    private StudentCvRepository studentCvRepository;

    @Mock
    private CvNotificationService cvNotificationService;

    @Mock
    private GestionnaireMapper gestionnaireMapper;

    @InjectMocks
    private GestionnaireService gestionnaireService;

    private Student testStudent;
    private StudentCv testCv;
    private byte[] pdfBytes;

    @BeforeEach
    void setUp() {
        // ARRANGE
        testStudent = new Student();
        testStudent.setId(1L);

        pdfBytes = new byte[] {37, 80, 68, 70, 45}; // %PDF-

        testCv = new StudentCv();
        testCv.setId(10L);
        testCv.setFileName("cv.pdf");
        testCv.setContent(pdfBytes);
        testCv.setStatus(Status.PENDING);
        testCv.setStudent(testStudent);
    }

    @Test
    void getPendingCvs_returnsMappedList() {
        // ARRANGE
        PendingCvDto dto = PendingCvDto.builder().id(10L).fileName("cv.pdf").build();
        when(studentCvRepository.findAllByStatusOrderByUploadedAtAsc(Status.PENDING)).thenReturn(List.of(testCv));
        when(gestionnaireMapper.toPendingCvDto(testCv)).thenReturn(dto);

        // ACT
        List<PendingCvDto> result = gestionnaireService.getPendingCvs();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals("cv.pdf", result.getFirst().getFileName());
        verify(studentCvRepository, times(1)).findAllByStatusOrderByUploadedAtAsc(Status.PENDING);
    }

    @Test
    void getPendingCvs_returnsEmptyList() {
        // ARRANGE
        when(studentCvRepository.findAllByStatusOrderByUploadedAtAsc(Status.PENDING)).thenReturn(List.of());

        // ACT
        List<PendingCvDto> result = gestionnaireService.getPendingCvs();

        // ASSERT
        assertTrue(result.isEmpty());
    }

    @Test
    void getCvContent_success_returnsByteArray() {
        // ARRANGE
        when(studentCvRepository.findById(10L)).thenReturn(Optional.of(testCv));

        // ACT
        byte[] result = gestionnaireService.getCvContent(10L);

        // ASSERT
        assertArrayEquals(pdfBytes, result);
        verify(studentCvRepository, times(1)).findById(10L);
    }

    @Test
    void getCvContent_not_Found_throwsInvalidCvException() {
        // ARRANGE
        when(studentCvRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(InvalidCvException.class, () -> gestionnaireService.getCvContent(99L));
        verify(studentCvRepository, times(1)).findById(99L);
    }

    @Test
    void acceptCv_success_setStatusAndCreatesNotification() {
        // ARRANGE
        when(studentCvRepository.findById(10L)).thenReturn(Optional.of(testCv));

        // ACT
        gestionnaireService.acceptCv(10L);

        // ASSERT
        assertEquals(Status.ACCEPTED, testCv.getStatus());
        verify(cvNotificationService, times(1)).notifyStudentOnCvAccepted(testStudent, testCv);
    }

    @Test
    void declineCv_success_setStatusAndReviewAndCreatesNotification() {
        // ARRANGE
        byte[] reviewBytes = new byte[] {1, 2 ,3};
        String base64Review = Base64.getEncoder().encodeToString(reviewBytes);
        CvUploadDto reviewRequest = new CvUploadDto("review.pdf", "application/pdf", base64Review);

        when(studentCvRepository.findById(10L)).thenReturn(Optional.of(testCv));

        // ACT
        gestionnaireService.declineCv(10L, reviewRequest);

        // ASSERT
        assertEquals(Status.DECLINED, testCv.getStatus());
        assertArrayEquals(reviewBytes, testCv.getReviewContent());
        assertEquals("review.pdf", testCv.getReviewFileName());
        assertEquals("application/pdf", testCv.getReviewContentType());
        verify(studentCvRepository, times(1)).save(testCv);
        verify(cvNotificationService, times(1)).notifyStudentOnCvDeclined(testStudent, testCv);
    }

    @Test
    void declineCv_notFound_throwsInvalidCvException() {
        // ARRANGE
        String base64Review = Base64.getEncoder().encodeToString(new byte[]{1});
        CvUploadDto reviewRequest = new CvUploadDto("review.pdf", "application/pdf", base64Review);

        when(studentCvRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(InvalidCvException.class, () -> gestionnaireService.declineCv(99L, reviewRequest));
        verify(studentCvRepository, never()).save(any());
        verify(cvNotificationService, never()).notifyStudentOnCvDeclined(any(), any());
    }
}

