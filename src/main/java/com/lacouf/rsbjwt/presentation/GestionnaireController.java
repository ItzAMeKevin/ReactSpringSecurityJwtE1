package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestionnaire")
@RequiredArgsConstructor
public class GestionnaireController {

    private final GestionnaireService gestionnaireService;

    @GetMapping("/offres/pending")
    public ResponseEntity<List<JobOfferDetailDTO>> getPendingOffers() {
        return ResponseEntity.ok(gestionnaireService.getPendingOffers());
    }

    @PutMapping("/offres/{id}/accept")
    public ResponseEntity<JobOfferDetailDTO> acceptOffer(@PathVariable Long id) {
        return gestionnaireService.acceptOffer(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/offres/{id}/refuse")
    public ResponseEntity<JobOfferDetailDTO> refuseOffer(@PathVariable Long id) {
        return gestionnaireService.refuseOffer(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
