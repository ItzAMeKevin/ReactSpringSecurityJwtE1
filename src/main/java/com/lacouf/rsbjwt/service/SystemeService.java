package com.lacouf.rsbjwt.service;


import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.mapper.NotificationMapper;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class SystemeService{

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ManagerRepository managerRepository;


    public List<NotificationDto> getNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        return notificationMapper.toDtoList(notificationRepository.findByUser_Credentials_Email(userEmail));
    }

    public void notifyManagers(NotificationType type, Long referenceId, String messageKey) {
        List<Notification> notifications = managerRepository.findAll().stream()
                .map(manager -> Notification.builder()
                        .user(manager)
                        .notificationType(type)
                        .referenceId(referenceId)
                        .message(messageKey)
                        .build())
                .toList();
        notificationRepository.saveAll(notifications);
    }

    public NotificationDto updateNotificationStatus(Authentication authentication, Long notificationId) throws NotificationNotFound {
        String userEmail = authentication.getName();
        return notificationRepository.findByIdAndUser_Credentials_Email(notificationId, userEmail)
                .map(notification -> {
                    notification.setStatus(Status.READ);
                    return notificationMapper.toDto(notificationRepository.save(notification));
                })
                .orElseThrow(NotificationNotFound::new);
    }
}

