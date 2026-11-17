package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import com.lacouf.rsbjwt.service.dto.UpdateCandidateStatusDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employer")
public class EmployerController {
    private final EmployerService employerService;

    @PostMapping("/addJobOffer")
    public ResponseEntity<JobOfferDetailDTO> addJobOffer(
            @Valid @RequestBody JobOfferCreateDTO jobOfferCreateDTO,
            Authentication authentication) {
        JobOfferDetailDTO created = employerService.addJobOffer(jobOfferCreateDTO, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping()
    public List<JobOfferDetailDTO> getJobOffer(Authentication authentication) {
        return employerService.getJobOffres(authentication.getName());
    }

    @PutMapping("/updateJobOffer/{id}")
    public ResponseEntity<JobOfferDetailDTO> updateJobOffer(
            @PathVariable Long id,
            @Valid @RequestBody JobOfferCreateDTO jobOfferCreateDTO,
            Authentication authentication) {
        return employerService.updateRefusedJobOffer(id, jobOfferCreateDTO, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{offerId}/candidates")
    public ResponseEntity<List<CandidateDto>> getCandidates(
            @PathVariable Long offerId,
            Authentication authentication) {
        return ResponseEntity.ok(employerService.getCandidates(offerId, authentication.getName()));
    }

    @PutMapping("/{offerId}/candidates/{cvId}/status")
    public ResponseEntity<CandidateDto> updateCandidateStatus(
            @PathVariable Long offerId,
            @PathVariable Long cvId,
            @Valid @RequestBody UpdateCandidateStatusDto request,
            Authentication authentication) {
        return ResponseEntity.ok(
                employerService.updateCandidateStatus(offerId, cvId, request.status(), authentication.getName()));
    }

    @GetMapping("/candidates/{cvId}/cv")
    public ResponseEntity<byte[]> getCandidateCv(
            @PathVariable Long cvId,
            Authentication authentication) {
        byte[] content = employerService.getCandidateCv(cvId, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);
    }
}
