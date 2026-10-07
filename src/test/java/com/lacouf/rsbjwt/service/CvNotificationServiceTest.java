package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.ManagerNotFoundException;
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
public class CvNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private StudentCvRepository studentCvRepository;

    @InjectMocks
    private CvNotificationService cvNotificationService;

    private Student testStudent;
    private StudentCv testCv;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(2L);
        testStudent.setFirstName("Isidor");
        testStudent.setLastName("Teurteur");
        testStudent.setMatricule("ETUD-001");

        testCv = new StudentCv();
        testCv.setId(7L);
        testCv.setFileName("cv LeonM.pdf");
        testCv.setStudent(testStudent);
    }

    private Notification captureSavedNotification() {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        return captor.getValue();
    }

    private Manager newManager(Long id) {
        Manager manager = new Manager();
        manager.setId(id);
        return manager;
    }

    @Test
    void notifyManagerOnCvUpload_assignedManager_notifiesThatManager() {
        // ARRANGE
        Manager assigned = newManager(1L);
        testStudent.setAssignedManager(assigned);
        when(studentCvRepository.findById(7L)).thenReturn(Optional.of(testCv));

        // ACT
        cvNotificationService.notifyManagerOnCvUpload(7L);

        // ASSERT
        Notification saved = captureSavedNotification();
        assertSame(assigned, saved.getManager());
        assertSame(testCv, saved.getStudentCv());
        assertEquals(NotificationType.CV_UPLOADED, saved.getType());
        assertEquals(NotificationStatus.SENT, saved.getStatus());
        assertEquals(0, saved.getRetryCount());
        assertNotNull(saved.getSentAt());
        verifyNoInteractions(managerRepository);
    }

    @Test
    void notifyManagerOnCvUpload_message_containsStudentIdentityAndFileName() {
        // ARRANGE
        testStudent.setAssignedManager(newManager(1L));
        when(studentCvRepository.findById(7L)).thenReturn(Optional.of(testCv));

        // ACT
        cvNotificationService.notifyManagerOnCvUpload(7L);

        // ASSERT
        String message = captureSavedNotification().getMessage();
        assertTrue(message.contains("Isidor"));
        assertTrue(message.contains("Teurteur"));
        assertTrue(message.contains("ETUD-001"));
        assertTrue(message.contains("cv LeonM.pdf"));
    }

    @Test
    void notifyManagerOnCvUpload_noAssignedManager_fallsBackToFirstManager() {
        // ARRANGE
        Manager first = newManager(1L);
        Manager second = newManager(2L);
        when(studentCvRepository.findById(7L)).thenReturn(Optional.of(testCv));
        when(managerRepository.findAll()).thenReturn(List.of(first, second));

        // ACT
        cvNotificationService.notifyManagerOnCvUpload(7L);

        // ASSERT
        assertSame(first, captureSavedNotification().getManager());
    }

    @Test
    void notifyManagerOnCvUpload_noManagerInSystem_throwsAndSavesNothing() {
        // ARRANGE
        when(studentCvRepository.findById(7L)).thenReturn(Optional.of(testCv));
        when(managerRepository.findAll()).thenReturn(List.of());

        // ACT & ASSERT
        assertThrows(ManagerNotFoundException.class, () -> cvNotificationService.notifyManagerOnCvUpload(7L));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void notifyManagerOnCvUpload_unknownCv_throwsIllegalState() {
        // ARRANGE
        when(studentCvRepository.findById(7L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> cvNotificationService.notifyManagerOnCvUpload(7L));
        verifyNoInteractions(notificationRepository, managerRepository);
    }

    @Test
    void notifyManagerOnCvUpload_saveFails_exceptionPropagatesToCaller() {
        // ARRANGE
        testStudent.setAssignedManager(newManager(1L));
        when(studentCvRepository.findById(7L)).thenReturn(Optional.of(testCv));
        when(notificationRepository.save(any())).thenThrow(new RuntimeException("db down"));

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> cvNotificationService.notifyManagerOnCvUpload(7L));
    }

    @Test
    void notifyStudentOnCvAccepted_createsStudentNotification() {
        // ACT
        cvNotificationService.notifyStudentOnCvAccepted(testStudent, testCv);

        // ASSERT
        Notification saved = captureSavedNotification();
        assertSame(testStudent, saved.getStudent());
        assertSame(testCv, saved.getStudentCv());
        assertNull(saved.getManager());
        assertEquals(NotificationType.CV_ACCEPTED, saved.getType());
        assertEquals(NotificationStatus.SENT, saved.getStatus());
        assertTrue(saved.getMessage().contains("cv LeonM.pdf"));
        assertNotNull(saved.getSentAt());
    }

    @Test
    void notifyStudentOnCvDeclined_createsRejectedNotification() {
        // ACT
        cvNotificationService.notifyStudentOnCvDeclined(testStudent, testCv);

        // ASSERT
        Notification saved = captureSavedNotification();
        assertSame(testStudent, saved.getStudent());
        assertEquals(NotificationType.CV_REJECTED, saved.getType());
        assertEquals(NotificationStatus.SENT, saved.getStatus());
        assertTrue(saved.getMessage().contains("cv LeonM.pdf"));
    }

    @Test
    void notifyStudentOnCvAccepted_saveFails_doesNotThrow() {
        // ARRANGE
        when(notificationRepository.save(any())).thenThrow(new RuntimeException("db down"));

        // ACT & ASSERT
        assertDoesNotThrow(() -> cvNotificationService.notifyStudentOnCvAccepted(testStudent, testCv));
    }

    @Test
    void notifyStudentOnCvDeclined_saveFails_doesNotThrow() {
        // ARRANGE
        when(notificationRepository.save(any())).thenThrow(new RuntimeException("db down"));

        // ACT & ASSERT
        assertDoesNotThrow(() -> cvNotificationService.notifyStudentOnCvDeclined(testStudent, testCv));
    }
}