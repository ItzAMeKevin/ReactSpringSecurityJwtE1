package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.service.NotificationService;
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
public class NotificationController {
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(Authentication authentication) {
        try {
            List<NotificationDto> notifications = notificationService.getNotifications(authentication);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping
    public ResponseEntity<NotificationDto> updateNotification(Authentication authentication, Long notificationId) {
       try {
            NotificationDto updatedNotification = notificationService.updateNotificationStatus(authentication, notificationId);
            return ResponseEntity.ok(updatedNotification);
        } catch (NotificationNotFound e) {
           NotificationDto errorResponse = NotificationDto.builder()
                     .erreur(e.getMessage())
                      .build();

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

    }

}
