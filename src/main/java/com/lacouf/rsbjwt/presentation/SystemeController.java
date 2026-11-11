package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.service.SystemeService;
import com.lacouf.rsbjwt.service.dto.NotificationDto;

import java.util.List;

import org.springframework.security.core.Authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class SystemeController {
    private final SystemeService notificationService;
    
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getNotifications(authentication));
    }

    @PatchMapping
    public ResponseEntity<NotificationDto> updateNotification(Authentication authentication, Long notificationId)
            throws NotificationNotFound {
       return ResponseEntity.ok(notificationService.updateNotificationStatus(authentication, notificationId));
    }

}
