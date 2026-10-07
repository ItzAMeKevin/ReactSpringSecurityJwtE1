package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.EmployerNotification;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.repository.EmployerNotificationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private Employer testEmployer;
    private JobOffer testOffer;

    @BeforeEach
    void setUp() {
        // ARRANGE
        testEmployer = new Employer();

        testOffer = new JobOffer();
        testOffer.setId(1L);
        testOffer.setTitle("Stagiaire développeur web");
        testOffer.setStatus(OfferStatus.WAITING);
        testOffer.setEmployer(testEmployer);
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
}