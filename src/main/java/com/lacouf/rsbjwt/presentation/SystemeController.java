package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.SystemeService;
import com.lacouf.rsbjwt.service.dto.NotificationDto;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class SystemeController {
    private final SystemeService systemeService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(Authentication authentication) {
        return ResponseEntity.ok(systemeService.getNotifications(authentication));
    }

    @PatchMapping
    public ResponseEntity<NotificationDto> updateNotification(Authentication authentication, Long notificationId) {
       return ResponseEntity.ok(systemeService.updateNotificationStatus(authentication, notificationId));
    }

    @PostMapping("/signature")
    public ResponseEntity<Void> uploadSignature(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        systemeService.saveSignature(authentication, file.getBytes(), file.getContentType());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/signature")
    public ResponseEntity<byte[]> getSignature(Authentication authentication) {
        byte[] image = systemeService.getSignatureImage(authentication);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

}
