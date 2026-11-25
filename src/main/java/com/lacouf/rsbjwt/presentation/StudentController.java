package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.StudentCvService;
import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import com.lacouf.rsbjwt.service.dto.InterviewDto;
import com.lacouf.rsbjwt.service.dto.InterviewRespondRequest;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etudiant")
@RequiredArgsConstructor
public class StudentController {

    private final StudentCvService studentCvService;
    private final StudentService studentService;

    // ── CV endpoints ──────────────────────────────────────────────────────────

    @PostMapping("/cv")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<CvMetaDataDto> upload(Authentication authentication,
                                                @Valid @RequestBody CvUploadDto request) {
        CvMetaDataDto response = studentCvService.upload(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/cv")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<CvMetaDataDto>> listCvs(Authentication authentication) {
        List<CvMetaDataDto> response = studentCvService.listCvs(authentication.getName());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/cv/{cvId}/content")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<byte[]> getCvContent(@PathVariable Long cvId, Authentication authentication) {
        byte[] content = studentCvService.getCvContent(cvId, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);
    }

    @GetMapping("/cv/{cvId}/review")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<String> getReviewFile(@PathVariable Long cvId, Authentication authentication) {
        String feedback = studentCvService.getReviewFile(cvId, authentication.getName());
        if (feedback == null || feedback.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(feedback);
    }

    @PostMapping("/cv/{offerID}/appliquer")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<String> appliquerOffre(@PathVariable long offerID, @Valid @RequestBody CvMetaDataDto cvDto) {
        studentCvService.applyToJobOffer(offerID, cvDto);
        return ResponseEntity.ok("Candidature soumise avec succès.");
    }

    // ── Offer / interview endpoints ───────────────────────────────────────────

    @GetMapping("/offres")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<JobOfferDetailDTO>> getAcceptedOffers(Authentication authentication) {
        return ResponseEntity.ok(studentService.getAcceptedJobOffers(authentication.getName()));
    }

    @GetMapping("/interviews")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<InterviewDto>> getInterviews(Authentication authentication) {
        return ResponseEntity.ok(studentService.getInterviews(authentication.getName()));
    }

    @PutMapping("/interviews/{id}/respond")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<InterviewDto> respondToInterview(@PathVariable Long id,
                                                           @Valid @RequestBody InterviewRespondRequest request,
                                                           Authentication authentication) {
        return ResponseEntity.ok(studentService.respondToInterview(id, request, authentication.getName()));
    }
}
