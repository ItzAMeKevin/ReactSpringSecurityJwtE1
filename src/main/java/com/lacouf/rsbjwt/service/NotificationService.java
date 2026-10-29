package com.lacouf.rsbjwt.service;


import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.mapper.NotificationMapper;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService{

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    public List<NotificationDto> getNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        return notificationRepository.findByUserEmail(userEmail)
                .map(notificationMapper::toDtoList)
                .orElse(Collections.emptyList());
    }

    public NotificationDto updateNotificationStatus(Authentication authentication, Long notificationId) throws NotificationNotFound {
        String userEmail = authentication.getName();
        return notificationRepository.findByIdAndUserEmail(notificationId, userEmail)
                .map(notification -> {
                    notification.setStatus(Status.READ);
                    return notificationMapper.toDto(notificationRepository.save(notification));
                })
                .orElseThrow(NotificationNotFound::new);
    }
}

