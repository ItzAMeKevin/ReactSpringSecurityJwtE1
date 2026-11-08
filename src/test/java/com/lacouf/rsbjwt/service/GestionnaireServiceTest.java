package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.CvMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferNotFoundException;
import com.lacouf.rsbjwt.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionnaireServiceTest {

    @Mock private JobOfferRepository jobOfferRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private StudentCvRepository studentCvRepository;
    @Mock private CvMapper cvMapper;
    @Mock private JobOfferMapper jobOfferMapper;
    @InjectMocks private GestionnaireService service;

    @Test
    void getsPendingCvsAndContent() {
        StudentCv cv = new StudentCv();
        cv.setContent(new byte[]{1, 2});
        PendingCvDto dto = PendingCvDto.builder().id(1L).build();
        when(studentCvRepository.findAllByStatusOrderByUploadedAtAsc(Status.PENDING)).thenReturn(List.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(dto);

        assertEquals(List.of(dto), service.getPendingCvs());
        when(studentCvRepository.findById(1L)).thenReturn(Optional.of(cv));
        assertArrayEquals(new byte[]{1, 2}, service.getCvContent(1L));
    }

    @Test
    void rejectsMissingCvContent() {
        when(studentCvRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InvalidCvException.class, () -> service.getCvContent(1L));
    }

    @Test
    void acceptsAndDeclinesCvWithNotifications() {
        Student student = new Student();
        StudentCv cv = new StudentCv();
        cv.setId(4L);
        cv.setStudent(student);
        when(studentCvRepository.findById(4L)).thenReturn(Optional.of(cv));
        when(cvMapper.toDto(cv)).thenReturn(PendingCvDto.builder().id(4L).build());

        assertEquals(4L, service.acceptCv(4L).getId());
        assertEquals(Status.ACCEPTED, cv.getStatus());
        verify(studentCvRepository).save(cv);
        verify(notificationRepository).save(any(Notification.class));

        FeedbackDto feedback = new FeedbackDto("Needs changes");
        service.declineCv(4L, feedback);
        assertEquals(Status.DECLINED, cv.getStatus());
        assertEquals("Needs changes", cv.getReviewFeedback());
        verify(studentCvRepository, times(2)).findById(4L);
    }

    @Test
    void changesOfferStatusAndRejectsInvalidOffers() throws Throwable {
        JobOffer offer = new JobOffer();
        offer.setId(8L);
        offer.setStatus(Status.PENDING);
        offer.setEmployer(new Employer());
        JobOfferDto dto = JobOfferDto.builder().id(8L).build();
        when(jobOfferRepository.findById(8L)).thenReturn(Optional.of(offer));
        when(jobOfferMapper.toDtoSimple(offer)).thenReturn(dto);

        assertEquals(dto, service.acceptOffer(8L));
        assertEquals(Status.ACCEPTED, offer.getStatus());

        assertThrows(InvalidStateTransitionException.class, () -> service.refuseOffer(8L, new FeedbackDto("feedback")));
        when(jobOfferRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(JobOfferNotFoundException.class, () -> service.acceptOffer(9L));
    }

    @Test
    void refusesOfferWithFeedback() throws Throwable {
        JobOffer offer = new JobOffer();
        offer.setId(10L);
        offer.setStatus(Status.PENDING);
        offer.setEmployer(new Employer());
        JobOfferDto dto = JobOfferDto.builder().id(10L).build();
        when(jobOfferRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(jobOfferMapper.toDtoSimple(offer)).thenReturn(dto);

        FeedbackDto feedback = new FeedbackDto("Not suitable");
        assertEquals(dto, service.refuseOffer(10L, feedback));
        assertEquals(Status.DECLINED, offer.getStatus());
        assertEquals("Not suitable", offer.getRefusalFeedback());
        verify(jobOfferRepository).save(offer);
        verify(notificationRepository).save(any(Notification.class));
    }
}
