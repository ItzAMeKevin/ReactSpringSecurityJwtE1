package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.EmployerNotification;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.repository.EmployerNotificationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import com.lacouf.rsbjwt.model.CvStatus;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GestionnaireServiceTest {

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private EmployerNotificationRepository employerNotificationRepository;

    @Mock
    private JobOfferMapper jobOfferMapper;

    @InjectMocks
    private GestionnaireService gestionnaireService;

    @Mock
    private StudentCvRepository studentCvRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private GestionnaireMapper gestionnaireMapper;

    private Employer testEmployer;
    private JobOffer testOffer;
    private Student testStudent;
    private StudentCv testCv;
    private byte[] pdfBytes;

    @BeforeEach
    void setUp() {
        // ARRANGE
        testEmployer = new Employer();
        testStudent = new Student();
        testStudent.setId(1L);
        pdfBytes = new byte[] {37, 80, 68, 70, 45};

        testOffer = new JobOffer();
        testOffer.setId(1L);
        testOffer.setTitle("Stagiaire développeur web");
        testOffer.setStatus(OfferStatus.WAITING);
        testOffer.setEmployer(testEmployer);

        testCv = new StudentCv();
        testCv.setId(10L);
        testCv.setFileName("cv.pdf");
        testCv.setContent(pdfBytes);
        testCv.setStatus(CvStatus.PENDING);
        testCv.setStudent(testStudent);

    }

    // ---------- getPendingOffers ----------

    @Test
    void testGetPendingOffersSuccess() {
        // ARRANGE
        JobOffer secondOffer = new JobOffer();
        secondOffer.setId(2L);
        secondOffer.setTitle("Stagiaire réseaux");
        secondOffer.setStatus(OfferStatus.WAITING);

        JobOfferDetailDTO dto1 = mock(JobOfferDetailDTO.class);
        JobOfferDetailDTO dto2 = mock(JobOfferDetailDTO.class);

        when(jobOfferRepository.findAllByStatus(OfferStatus.WAITING))
                .thenReturn(List.of(testOffer, secondOffer));
        when(jobOfferMapper.toDto(testOffer)).thenReturn(dto1);
        when(jobOfferMapper.toDto(secondOffer)).thenReturn(dto2);

        // ACT
        List<JobOfferDetailDTO> result = gestionnaireService.getPendingOffers();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));
        verify(jobOfferRepository, times(1)).findAllByStatus(OfferStatus.WAITING);
        verify(jobOfferMapper, times(2)).toDto(any(JobOffer.class));
    }

    @Test
    void testGetPendingOffersEmptyWhenNoneWaiting() {
        // ARRANGE
        when(jobOfferRepository.findAllByStatus(OfferStatus.WAITING))
                .thenReturn(List.of());

        // ACT
        List<JobOfferDetailDTO> result = gestionnaireService.getPendingOffers();

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jobOfferMapper, never()).toDto(any());
    }

    // ---------- acceptOffer ----------

    @Test
    void testAcceptOfferSuccess() {
        // ARRANGE
        JobOfferDetailDTO dto = mock(JobOfferDetailDTO.class);

        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(jobOfferMapper.toDto(testOffer)).thenReturn(dto);

        // ACT
        Optional<JobOfferDetailDTO> result = gestionnaireService.acceptOffer(1L);

        // ASSERT
        assertTrue(result.isPresent());
        assertSame(dto, result.get());
        assertEquals(OfferStatus.ACCEPTED, testOffer.getStatus());
        verify(jobOfferRepository, times(1)).save(testOffer);
        verify(employerNotificationRepository, times(1)).save(any(EmployerNotification.class));
    }

    @Test
    void testAcceptOfferCreatesNotificationForEmployer() {
        // ARRANGE
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(jobOfferMapper.toDto(testOffer)).thenReturn(mock(JobOfferDetailDTO.class));

        // ACT
        gestionnaireService.acceptOffer(1L);

        // ASSERT
        ArgumentCaptor<EmployerNotification> captor = ArgumentCaptor.forClass(EmployerNotification.class);
        verify(employerNotificationRepository).save(captor.capture());
        EmployerNotification notification = captor.getValue();
        assertEquals("Offre acceptée", notification.getTitle());
        assertEquals("Votre offre \"Stagiaire développeur web\" a été acceptée.", notification.getMessage());
        assertSame(testEmployer, notification.getEmployer());
    }

    @Test
    void testAcceptOfferNotFoundReturnsEmpty() {
        // ARRANGE
        when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Optional<JobOfferDetailDTO> result = gestionnaireService.acceptOffer(99L);

        // ASSERT
        assertTrue(result.isEmpty());
        verify(jobOfferRepository, never()).save(any());
        verify(employerNotificationRepository, never()).save(any());
        verify(jobOfferMapper, never()).toDto(any());
    }

    // ---------- refuseOffer ----------

    @Test
    void testRefuseOfferSuccess() {
        // ARRANGE
        JobOfferDetailDTO dto = mock(JobOfferDetailDTO.class);

        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(jobOfferMapper.toDto(testOffer)).thenReturn(dto);

        // ACT
        Optional<JobOfferDetailDTO> result = gestionnaireService.refuseOffer(1L);

        // ASSERT
        assertTrue(result.isPresent());
        assertSame(dto, result.get());
        assertEquals(OfferStatus.REFUSED, testOffer.getStatus());
        verify(jobOfferRepository, times(1)).save(testOffer);
        verify(employerNotificationRepository, times(1)).save(any(EmployerNotification.class));
    }

    @Test
    void testRefuseOfferCreatesNotificationForEmployer() {
        // ARRANGE
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(jobOfferMapper.toDto(testOffer)).thenReturn(mock(JobOfferDetailDTO.class));

        // ACT
        gestionnaireService.refuseOffer(1L);

        // ASSERT
        ArgumentCaptor<EmployerNotification> captor = ArgumentCaptor.forClass(EmployerNotification.class);
        verify(employerNotificationRepository).save(captor.capture());
        EmployerNotification notification = captor.getValue();
        assertEquals("Offre refusée", notification.getTitle());
        assertEquals("Votre offre \"Stagiaire développeur web\" a été refusée.", notification.getMessage());
        assertSame(testEmployer, notification.getEmployer());
    }

    @Test
    void testRefuseOfferNotFoundReturnsEmpty() {
        // ARRANGE
        when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Optional<JobOfferDetailDTO> result = gestionnaireService.refuseOffer(99L);

        // ASSERT
        assertTrue(result.isEmpty());
        verify(jobOfferRepository, never()).save(any());
        verify(employerNotificationRepository, never()).save(any());
        verify(jobOfferMapper, never()).toDto(any());
    }

    @Test
    void getPendingCvs_returnsMappedList() {
        // ARRANGE
        PendingCvDto dto = PendingCvDto.builder().id(10L).fileName("cv.pdf").build();
        when(studentCvRepository.findAllByStatusOrderByUploadedAtAsc(CvStatus.PENDING)).thenReturn(List.of(testCv));
        when(gestionnaireMapper.toPendingCvDto(testCv)).thenReturn(dto);

        // ACT
        List<PendingCvDto> result = gestionnaireService.getPendingCvs();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals("cv.pdf", result.getFirst().getFileName());
        verify(studentCvRepository, times(1)).findAllByStatusOrderByUploadedAtAsc(CvStatus.PENDING);
    }

    @Test
    void getPendingCvs_returnsEmptyList() {
        // ARRANGE
        when(studentCvRepository.findAllByStatusOrderByUploadedAtAsc(CvStatus.PENDING)).thenReturn(List.of());

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
    void getCvContent_not_Found_throwsUserNotFoundException() {
        // ARRANGE
        when(studentCvRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, () -> gestionnaireService.getCvContent(99L));
        verify(studentCvRepository, times(1)).findById(99L);
    }

    @Test
    void acceptCv_success_setStatusAndCreatesNotification() {
        // ARRANGE

        when(studentCvRepository.findById(10L)).thenReturn(Optional.of(testCv));
        // ACT
        gestionnaireService.acceptCv(10L);

        // ASSERT
        assertEquals(CvStatus.ACCEPTED, testCv.getStatus());
        verify(notificationRepository, times(1)).save(any(Notification.class));
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
        assertEquals(CvStatus.DECLINED, testCv.getStatus());
        assertArrayEquals(reviewBytes, testCv.getReviewContent());
        assertEquals("review.pdf", testCv.getReviewFileName());
        assertEquals("application/pdf", testCv.getReviewContentType());
        verify(studentCvRepository, times(1)).save(testCv);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void declineCv_notFound_throwsUserNotFoundException() {
        // ARRANGE
        String base64Review = Base64.getEncoder().encodeToString(new byte[]{1});
        CvUploadDto reviewRequest = new CvUploadDto("review.pdf", "application/pdf", base64Review);

        when(studentCvRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, () -> gestionnaireService.declineCv(99L, reviewRequest));
        verify(studentCvRepository, never()).save(any());
        verify(notificationRepository, never()).save(any());
    }
}