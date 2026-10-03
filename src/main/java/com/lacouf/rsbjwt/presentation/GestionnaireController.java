package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import com.lacouf.rsbjwt.service.dto.ManagerNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestionnaire")
@RequiredArgsConstructor
public class GestionnaireController {

    private final GestionnaireService gestionnaireService;

    @GetMapping("/offres/pending")
    public ResponseEntity<List<JobOfferDto>> getPendingOffers() {
        return ResponseEntity.ok(gestionnaireService.getPendingOffers());
    }

    @PutMapping("/offres/{id}/accept")
    public ResponseEntity<JobOfferDto> acceptOffer(@PathVariable Long id) {
        return gestionnaireService.acceptOffer(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/offres/{id}/refuse")
    public ResponseEntity<JobOfferDto> refuseOffer(@PathVariable Long id) {
        return gestionnaireService.refuseOffer(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<ManagerNotificationDto>> getNotifications(Authentication authentication) {
        return ResponseEntity.ok(gestionnaireService.getNotifications(authentication.getName()));
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<ManagerNotificationDto> markAsRead(
            @PathVariable Long id, Authentication authentication) {
        return gestionnaireService.markNotificationAsRead(authentication.getName(), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}