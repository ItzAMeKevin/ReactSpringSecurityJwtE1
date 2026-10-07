package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.NotificationStatus;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_INTERVAL_MINUTES = 5;

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void retryFailedNotifications() {
        log.info("Starting retry task for failed notifications");
        
        Instant fiveMinutesAgo = Instant.now().minus(RETRY_INTERVAL_MINUTES, ChronoUnit.MINUTES);
        
        notificationRepository.findAllByStatusAndLastRetryAtBeforeOrderByCreatedAtAsc(
            NotificationStatus.FAILED, 
            fiveMinutesAgo
        ).forEach(notification -> {
            if (notification.getRetryCount() < MAX_RETRY_ATTEMPTS) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setRetryCount(notification.getRetryCount() + 1);
                notification.setLastRetryAt(Instant.now());
                notification.setSentAt(Instant.now());
                notificationRepository.save(notification);
                log.info("Retry successful for notification {}, attempt {}",
                    notification.getId(), notification.getRetryCount());
            } else {
                log.warn("Max retry attempts reached for notification {}", notification.getId());
            }
        });
        
        log.info("Retry task completed");
    }
}
