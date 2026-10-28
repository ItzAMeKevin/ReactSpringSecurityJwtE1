package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.presentation.service.dto.NotificationDTO;

import java.util.List;

import org.springframework.security.core.Authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(Authentification authentication) {
        List<NotificationDTO> notifications = notificationService.getNotifications(authentication);
        return ResponseEntity.ok(notifications);
    }
}
