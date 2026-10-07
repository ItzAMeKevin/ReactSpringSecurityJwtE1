package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.NotificationStatus;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationSchedulerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    private Notification failedNotification(int retryCount) {
        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.FAILED);
        notification.setRetryCount(retryCount);
        return notification;
    }

    @Test
    void retryFailedNotifications_underMaxAttempts_incrementsCountAndMarksSent() {
        // ARRANGE
        Notification notification = failedNotification(1);
        when(notificationRepository.findAllByStatusAndLastRetryAtBeforeOrderByCreatedAtAsc(
                eq(NotificationStatus.FAILED), any(Instant.class))).thenReturn(List.of(notification));

        // ACT
        notificationScheduler.retryFailedNotifications();

        // ASSERT
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertEquals(2, notification.getRetryCount());
        assertNotNull(notification.getLastRetryAt());
        assertNotNull(notification.getSentAt());
        verify(notificationRepository, atLeastOnce()).save(notification);
    }

    @Test
    void retryFailedNotifications_maxAttemptsReached_leavesNotificationUntouched() {
        // ARRANGE
        Notification notification = failedNotification(3);
        when(notificationRepository.findAllByStatusAndLastRetryAtBeforeOrderByCreatedAtAsc(
                eq(NotificationStatus.FAILED), any(Instant.class))).thenReturn(List.of(notification));

        // ACT
        notificationScheduler.retryFailedNotifications();

        // ASSERT
        assertEquals(NotificationStatus.FAILED, notification.getStatus());
        assertEquals(3, notification.getRetryCount());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void retryFailedNotifications_nothingToRetry_savesNothing() {
        // ARRANGE
        when(notificationRepository.findAllByStatusAndLastRetryAtBeforeOrderByCreatedAtAsc(
                eq(NotificationStatus.FAILED), any(Instant.class))).thenReturn(List.of());

        // ACT
        notificationScheduler.retryFailedNotifications();

        // ASSERT
        verify(notificationRepository, never()).save(any());
    }
}