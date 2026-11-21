package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.mapper.NotificationMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemeServiceTest {

    @Mock private EmployerRepository employerRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private StudentCvRepository cvRepository;
    @Mock private JobOfferRepository jobOfferRepository;
    @Mock private NotificationRepository repository;
    @Mock private NotificationMapper mapper;
    @Mock private ManagerRepository managerRepository;
    @Mock private Authentication authentication;
    @InjectMocks private SystemeService service;

    @Test
    void getsNotificationsOrEmptyList() {
        when(authentication.getName()).thenReturn("user@test.com");
        NotificationDto dto = NotificationDto.builder().id(1L).build();
        when(repository.findByUser_Credentials_Email("user@test.com")).thenReturn(List.of(new Notification()));
        when(mapper.toDtoList(anyList())).thenReturn(List.of(dto));
        assertEquals(List.of(dto), service.getNotifications(authentication));

        when(repository.findByUser_Credentials_Email("user@test.com")).thenReturn(List.of());
        when(mapper.toDtoList(List.of())).thenReturn(List.of());
        assertTrue(service.getNotifications(authentication).isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void notifiesEveryManager() {
        Manager first = new Manager();
        Manager second = new Manager();
        when(managerRepository.findAll()).thenReturn(List.of(first, second));

        service.notifyManagers(NotificationType.CV, 5L, "notification.types.CV_UPLOADED.message");

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        List<Notification> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertSame(first, saved.get(0).getUser());
        assertSame(second, saved.get(1).getUser());
        saved.forEach(notification -> {
            assertEquals(NotificationType.CV, notification.getNotificationType());
            assertEquals(5L, notification.getReferenceId());
            assertEquals("notification.types.CV_UPLOADED.message", notification.getMessage());
            assertEquals(Status.UNREAD, notification.getStatus());
        });
    }

    @Test
    void updatesNotificationOrThrows() throws Exception {
        when(authentication.getName()).thenReturn("user@test.com");
        Notification notification = new Notification();
        NotificationDto dto = NotificationDto.builder().id(1L).build();
        when(repository.findByIdAndUser_Credentials_Email(1L, "user@test.com")).thenReturn(Optional.of(notification));
        when(repository.save(notification)).thenReturn(notification);
        when(mapper.toDto(notification)).thenReturn(dto);

        assertEquals(dto, service.updateNotificationStatus(authentication, 1L));
        assertEquals(Status.READ, notification.getStatus());

        when(repository.findByIdAndUser_Credentials_Email(2L, "user@test.com")).thenReturn(Optional.empty());
        assertThrows(NotificationNotFound.class,
                () -> service.updateNotificationStatus(authentication, 2L));
    }


    @Test
    @SuppressWarnings("unchecked")
    void notifiesAllEmployers() {
        Employer first = new Employer();
        Employer second = new Employer();
        when(employerRepository.findAll()).thenReturn(List.of(first, second));

        service.notifyAllEmployers(NotificationType.JOB_OFFER, 42L, "notification.types.JOB_OFFER.message");

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        List<Notification> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertSame(first, saved.get(0).getUser());
        assertSame(second, saved.get(1).getUser());
        saved.forEach(n -> {
            assertEquals(NotificationType.JOB_OFFER, n.getNotificationType());
            assertEquals(42L, n.getReferenceId());
            assertEquals("notification.types.JOB_OFFER.message", n.getMessage());
            assertEquals(Status.UNREAD, n.getStatus());
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void notifiesAllStudents() {
        Student first = new Student();
        Student second = new Student();
        when(studentRepository.findAll()).thenReturn(List.of(first, second));

        service.notifyAllStudents(NotificationType.CV, 7L, "notification.types.CV_UPLOADED.message");

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        List<Notification> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertSame(first, saved.get(0).getUser());
        assertSame(second, saved.get(1).getUser());
        saved.forEach(n -> {
            assertEquals(NotificationType.CV, n.getNotificationType());
            assertEquals(7L, n.getReferenceId());
            assertEquals(Status.UNREAD, n.getStatus());
        });
    }
    @Test
    @SuppressWarnings("unchecked")
    void notifiesStudentsOfExistingOffer() {
        Programe programe = Programe.TECHNIQUES_INFORMATIQUE;
        JobOffer offer = new JobOffer();
        offer.setPrograme(programe);

        Student student1 = new Student();
        Student student2 = new Student();

        when(jobOfferRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(studentRepository.findByPrograme(programe)).thenReturn(List.of(student1, student2));

        service.notifyStudentsOfOffer(10L, "notification.types.JOB_OFFER.message");

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        List<Notification> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertSame(student1, saved.get(0).getUser());
        assertSame(student2, saved.get(1).getUser());
        saved.forEach(n -> {
            assertEquals(NotificationType.JOB_OFFER, n.getNotificationType());
            assertEquals(10L, n.getReferenceId());
            assertEquals("notification.types.JOB_OFFER.message", n.getMessage());
        });
    }
    @Test
    void doesNotNotifyWhenOfferNotFound() {
        when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

        service.notifyStudentsOfOffer(99L, "notification.types.JOB_OFFER.message");

        verify(repository, never()).saveAll(any());
        verify(studentRepository, never()).findByPrograme(any());
    }
    @Test
    void doesNotSaveWhenNoManagers() {
        when(managerRepository.findAll()).thenReturn(List.of());

        service.notifyManagers(NotificationType.CV, 1L, "notification.types.CV_UPLOADED.message");

        verify(repository, never()).saveAll(any());
    }
}
