package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etudiant")
@RequiredArgsConstructor
public class StudentNotificationController {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final GestionnaireMapper gestionnaireMapper;

    @GetMapping("/notifications")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<NotificationDto>> getNotifications(Authentication authentication) {
        Student student = studentRepository.findByCredentialsEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        List<NotificationDto> notifications = notificationRepository
                .findAllByStudent_IdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(gestionnaireMapper::toNotificationDtoWithStudentCv)
                .toList();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(notifications);
    }

    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId,
                                           Authentication authentication) {
        Student student = studentRepository.findByCredentialsEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        notificationRepository.findByIdAndStudent_Id(notificationId, student.getId())
                .ifPresent(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
        return ResponseEntity.noContent().build();
    }
}