package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.StudentCvService;
import com.lacouf.rsbjwt.service.SystemeService;
import com.lacouf.rsbjwt.service.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@RestController
@RequestMapping("/gestionnaire")
@RequiredArgsConstructor
public class GestionnaireController {

    private final GestionnaireService gestionnaireService;
    private final SystemeService systemeService;
    private final StudentCvService studentCvService;

    // ── CV Management ─────────────────────────────────────────────────────────

    @GetMapping("/cv/pending")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<PendingCvDto>> getPendingCvs() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gestionnaireService.getPendingCvs());
    }

    @GetMapping("/cv/{cvId}/content")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<byte[]> getCvContent(@PathVariable Long cvId) {
        try {
            byte[] content = gestionnaireService.getCvContent(cvId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cv/{cvId}/accept")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> acceptCv(@PathVariable Long cvId) {
        try {
            gestionnaireService.acceptCv(cvId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cv/{cvId}/decline")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> declineCv(@PathVariable Long cvId, @Valid @RequestBody CvDeclineFeedbackDto feedbackRequest) {
        try {
            gestionnaireService.declineCv(cvId, feedbackRequest);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── Job Offer Management ──────────────────────────────────────────────────

    @GetMapping("/offres/pending")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<JobOfferDto>> getPendingOffers() {
        return ResponseEntity.ok(gestionnaireService.getPendingOffers());
    }

    @PutMapping("/offres/{id}/accept")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<JobOfferDto> acceptOffer(@PathVariable Long id) {
        return gestionnaireService.acceptOffer(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/offres/{id}/refuse")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<JobOfferDto> refuseOffer(@PathVariable Long id) {
        return gestionnaireService.refuseOffer(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Manager Notifications (job-offer events) ──────────────────────────────

    @GetMapping("/offres/notifications")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<NotificationDto>> getNotifications(Authentication authentication) {
        return ResponseEntity.ok(systemeService.getNotifications(authentication));
    }

    @PutMapping("/offres/notifications/{id}/read")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<NotificationDto> markAsRead(
            @PathVariable Long id, Authentication authentication) {
        try {
            return ResponseEntity.ok(systemeService.updateNotificationStatus(authentication, id));
        } catch (NotificationNotFound e) {
            return ResponseEntity.notFound().build();
        }
    }
}
