package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.InvalidCvException;
import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferNotFoundException;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.StudentCvService;
import com.lacouf.rsbjwt.service.SystemeService;
import com.lacouf.rsbjwt.service.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        } catch (InvalidCvException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cv/{cvId}/accept")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<PendingCvDto> acceptCv(@PathVariable Long cvId) {
        try {
            PendingCvDto cv = gestionnaireService.acceptCv(cvId);
            return ResponseEntity.ok().body(cv);
        } catch (InvalidCvException e) {
            return ResponseEntity.badRequest().body(PendingCvDto.builder().erreur("errors.cvNotFound").build());
        }
    }

    @PutMapping("/cv/{cvId}/decline")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<FeedbackDto> declineCv(@PathVariable Long cvId, @Valid @RequestBody FeedbackDto feedbackRequest) {
        try {
            gestionnaireService.declineCv(cvId, feedbackRequest);
            return ResponseEntity.ok(feedbackRequest);
        } catch (InvalidCvException e) {
            return ResponseEntity.badRequest().build();
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
        try {
            JobOfferDto jobOffer = gestionnaireService.acceptOffer(id);
            return ResponseEntity.ok(jobOffer);
        } catch (JobOfferNotFoundException e) {
            return ResponseEntity.badRequest().body(JobOfferDto.builder().erreur("errors.jobOfferNotFound").build());
        } catch (InvalidStateTransitionException e) {
            return ResponseEntity.badRequest().body(JobOfferDto.builder().erreur("errors.invalidStateTransition").build());
        }        
    }

    @PutMapping("/offres/{id}/refuse")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<JobOfferDto> refuseOffer(@PathVariable Long id, @Valid @RequestBody FeedbackDto feedbackRequest) {
        try {
            JobOfferDto jobOffer = gestionnaireService.refuseOffer(id, feedbackRequest);
            return ResponseEntity.ok(jobOffer);
        } catch (JobOfferNotFoundException e) {
            return ResponseEntity.badRequest().body(JobOfferDto.builder().erreur("errors.jobOfferNotFound").build());
        } catch (InvalidStateTransitionException e) {
            return ResponseEntity.badRequest().body(JobOfferDto.builder().erreur("errors.invalidStateTransition").build());
        }
    }
}
