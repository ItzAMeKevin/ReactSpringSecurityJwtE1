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
        try {
            List<NotificationDto> notifications = notificationService.getNotifications(authentication);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<NotificationDto> updateNotification(Authentication authentication, @org.springframework.web.bind.annotation.PathVariable Long notificationId) {
       try {
            NotificationDto updatedNotification = notificationService.updateNotificationStatus(authentication, notificationId);
            return ResponseEntity.ok(updatedNotification);
        } catch (NotificationNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

    }

}
