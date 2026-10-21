package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.CvDeclineFeedbackDto;
import com.lacouf.rsbjwt.service.event.CvDecisionEvent;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GestionnaireServiceTest {

    @Mock
    private StudentCvRepository studentCvRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

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
    void acceptCv_success_setStatusAndPublishesDecisionEvent() {
        // ARRANGE
        when(studentCvRepository.findById(10L)).thenReturn(Optional.of(testCv));

        // ACT
        gestionnaireService.acceptCv(10L);

        // ASSERT
        assertEquals(Status.ACCEPTED, testCv.getStatus());
        verify(eventPublisher, times(1)).publishEvent(new CvDecisionEvent(10L));
    }

    @Test
    void declineCv_success_setStatusAndFeedbackAndPublishesDecisionEvent() {
        // ARRANGE
        CvDeclineFeedbackDto feedbackRequest = new CvDeclineFeedbackDto("Le format du CV n'est pas correct.");
        when(studentCvRepository.findById(10L)).thenReturn(Optional.of(testCv));

        // ACT
        gestionnaireService.declineCv(10L, feedbackRequest);

        // ASSERT
        assertEquals(Status.DECLINED, testCv.getStatus());
        assertEquals("Le format du CV n'est pas correct.", testCv.getReviewFeedback());
        verify(studentCvRepository, times(1)).save(testCv);
        verify(eventPublisher, times(1)).publishEvent(new CvDecisionEvent(10L));
    }

    @Test
    void declineCv_notFound_throwsInvalidCvException() {
        // ARRANGE
        CvDeclineFeedbackDto feedbackRequest = new CvDeclineFeedbackDto("Feedback");
        when(studentCvRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(InvalidCvException.class, () -> gestionnaireService.declineCv(99L, feedbackRequest));
        verify(studentCvRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }
}

