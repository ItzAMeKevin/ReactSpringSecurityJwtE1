package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.dto.InterviewDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import com.lacouf.rsbjwt.service.dto.StudentInterviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etudiant")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/offres")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<JobOfferDetailDTO>> getAcceptedOffers(Authentication authentication) {
        return ResponseEntity.ok(studentService.getAcceptedJobOffers(authentication.getName()));
    }

    @GetMapping("/entrevues")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<InterviewDto>> getInterviews(Authentication authentication) {
        return ResponseEntity.ok(studentService.getInterviews(authentication.getName()));
    }

    @PutMapping("/entrevues/{id}/repondre")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<InterviewDto> respondToInterview(
            @PathVariable Long id,
            @RequestBody StudentInterviewResponseDto response,
            Authentication authentication) {
        return ResponseEntity.ok(studentService.respondToInterview(id, response, authentication.getName()));
    }
}