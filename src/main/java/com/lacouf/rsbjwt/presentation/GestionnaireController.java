package com.lacouf.rsbjwt.presentation;

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
        byte[] content = gestionnaireService.getCvContent(cvId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);
    }

    @PutMapping("/cv/{cvId}/accept")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<PendingCvDto> acceptCv(@PathVariable Long cvId) {
        return ResponseEntity.ok().body(gestionnaireService.acceptCv(cvId));
    }

    @PutMapping("/cv/{cvId}/decline")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<FeedbackDto> declineCv(@PathVariable Long cvId, @Valid @RequestBody FeedbackDto feedbackRequest) {
        gestionnaireService.declineCv(cvId, feedbackRequest);
        return ResponseEntity.ok(feedbackRequest);
    }

    // ── Candidature Tracking ──────────────────────────────────────────────────

    @GetMapping("/candidatures")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<JobApplicationSuiviDto>> getAllCandidatures() {
        return ResponseEntity.ok(gestionnaireService.getAllCandidatures());
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
        return ResponseEntity.ok(gestionnaireService.acceptOffer(id));
    }

    @PutMapping("/offres/{id}/refuse")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<JobOfferDto> refuseOffer(@PathVariable Long id, @Valid @RequestBody FeedbackDto feedbackRequest) {
        return ResponseEntity.ok(gestionnaireService.refuseOffer(id, feedbackRequest));
    }

    @PutMapping("/contract/create")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<ContractDto> createContract(@Valid @RequestBody ContractDto contractDto) {
        return ResponseEntity.ok(gestionnaireService.createContract(contractDto));
    }

    // ── Internship Site Evaluation ────────────────────────────────────────────

    @GetMapping("/milieux/evaluation")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<PendingInternshipSiteDto>> getPendingInternshipSites() {
        return ResponseEntity.ok(gestionnaireService.getPendingInternshipSites());
    }

    @PostMapping("/milieux/{id}/evaluation")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> submitInternshipSiteEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody InternshipSiteEvaluationDto dto) {
        gestionnaireService.submitInternshipSiteEvaluation(id, dto);
        return ResponseEntity.ok().build();
    }
}
