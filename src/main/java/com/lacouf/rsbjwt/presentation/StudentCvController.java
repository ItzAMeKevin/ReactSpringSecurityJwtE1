package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
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
public class StudentController {

    private final StudentCvService studentCvService;

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<CvMetaDataDto> upload(Authentication authentication,
                                                @Valid @RequestBody CvUploadDto request) {
        try {
            CvMetaDataDto response = studentCvService.upload(authentication.getName(), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (InvalidCvException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<CvMetaDataDto>> listCvs(Authentication authentication) {
        try {
            List<CvMetaDataDto> response = studentCvService.listCvs(authentication.getName());
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{cvId}/content")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<byte[]> getCvContent(@PathVariable Long cvId, Authentication authentication) {
        try {
            byte[] content = studentCvService.getCvContent(cvId, authentication.getName());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content);
        } catch (InvalidCvException | UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{cvId}/review")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<String> getReviewFile(@PathVariable Long cvId, Authentication authentication) {
        try {
            String feedback = studentCvService.getReviewFile(cvId, authentication.getName());
            if (feedback == null || feedback.isBlank()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(feedback);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
