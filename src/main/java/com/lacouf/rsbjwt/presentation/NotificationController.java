package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestionnaire")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final ManagerRepository managerRepository;
    private final GestionnaireMapper gestionnaireMapper;

    @GetMapping("/notifications")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<NotificationDto>> getNotifications(Authentication authentication) {
        try {
            String email = authentication.getName();
            Manager manager = managerRepository.findAll().stream()
                    .filter(m -> m.getCredentials() != null && email.equals(m.getCredentials().getEmail()))
                    .findFirst()
                    .orElse(null);
            
            if (manager == null) {
                log.warn("Manager not found for email: {}", email);
                return ResponseEntity.ok().body(new java.util.ArrayList<>());
            }
            
            log.info("Fetching notifications for manager: {}", manager.getId());
            List<NotificationDto> notifications = notificationRepository
                    .findManagerNotificationsWithPendingCvs(manager.getId())
                    .stream()
                    .map(gestionnaireMapper::toNotificationDtoForManager)
                    .toList();
            
            log.info("Returning {} notifications for manager", notifications.size());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(notifications);
        } catch (Exception e) {
            log.error("Error fetching notifications", e);
            return ResponseEntity.ok().body(new java.util.ArrayList<>());
        }
    }

    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId,
                                           Authentication authentication) {
        try {
            String email = authentication.getName();
            Manager manager = managerRepository.findAll().stream()
                    .filter(m -> m.getCredentials() != null && email.equals(m.getCredentials().getEmail()))
                    .findFirst()
                    .orElse(null);
            
            if (manager == null) {
                return ResponseEntity.status(404).build();
            }
            
            notificationRepository.findByIdAndManager_Id(notificationId, manager.getId())
                    .ifPresent(n -> {
                        n.setRead(true);
                        notificationRepository.save(n);
                    });
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            return ResponseEntity.status(500).build();
        }
    }
}
