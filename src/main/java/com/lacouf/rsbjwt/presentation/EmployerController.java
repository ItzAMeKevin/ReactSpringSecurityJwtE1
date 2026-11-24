package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.service.InterviewService;
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
    private final InterviewService interviewService;

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

    @PutMapping("/{offerId}/candidates/{cvId}/reject")
    public ResponseEntity<CandidateDto> rejectCandidate(
            @PathVariable Long offerId,
            @PathVariable Long cvId,
            Authentication authentication) {
        return ResponseEntity.ok(employerService.rejectCandidate(offerId, cvId, authentication.getName()));
    }

    @PutMapping("/{offerId}/candidates/{cvId}/hire")
    public ResponseEntity<CandidateDto> hireStudent(
            @PathVariable Long offerId,
            @PathVariable Long cvId,
            Authentication authentication) {
        return ResponseEntity.ok(employerService.hireStudent(offerId, cvId, authentication.getName()));
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

    @PostMapping("/interview/schedule")
    public ResponseEntity<InterviewResponseDto> scheduleInterview(
            @Valid @RequestBody ScheduleInterviewRequestDto request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewService.schedule(request, authentication.getName()));
    }

    @GetMapping("/interviews")
    public ResponseEntity<List<EmployerInterviewDto>> getInterviews(Authentication authentication) {
        return ResponseEntity.ok(interviewService.getEmployerInterviews(authentication.getName()));
    }
    
    @GetMapping("/candidates/hire-pending")
    public ResponseEntity<List<CandidatureDto>> getHirePendingCandidates(Authentication authentication) {
        return ResponseEntity.ok(employerService.getHirePendingCandidates(authentication.getName()));
    }
}
