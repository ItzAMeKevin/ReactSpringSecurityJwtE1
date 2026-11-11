package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.StudentCvService;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
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
@RequestMapping("/student-cv")
@RequiredArgsConstructor
public class StudentCvController {

    private final StudentCvService studentCvService;

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<CvMetaDataDto> upload(Authentication authentication,
                                                @Valid @RequestBody CvUploadDto request) {
        CvMetaDataDto response = studentCvService.upload(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<CvMetaDataDto>> listCvs(Authentication authentication) {
        List<CvMetaDataDto> response = studentCvService.listCvs(authentication.getName());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/{cvId}/content")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<byte[]> getCvContent(@PathVariable Long cvId, Authentication authentication) {
        byte[] content = studentCvService.getCvContent(cvId, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);
    }

    @GetMapping("/{cvId}/review")
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


    @PostMapping("/{offerID}/appliquer")
    public ResponseEntity<String> appliquerOffre(@RequestParam long offerID, @RequestBody CvMetaDataDto cvDto) {
        studentCvService.applyToJobOffer(offerID, cvDto);
        return ResponseEntity.ok("Candidature soumise avec succès.");
    }
}
