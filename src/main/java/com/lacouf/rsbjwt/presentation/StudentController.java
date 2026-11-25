package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.service.dto.InterviewDto;
import com.lacouf.rsbjwt.service.dto.InterviewRespondRequest;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etudiant")
@PreAuthorize("hasAuthority('STUDENT')")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/offres")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<JobOfferDetailDTO>> getAcceptedOffers(Authentication authentication) {
        return ResponseEntity.ok(studentService.getAcceptedJobOffers(authentication.getName()));
    }

    @GetMapping("/embauches")
    public ResponseEntity<List<CandidatureDto>> getPendingHireOffers(Authentication authentication) {
        return ResponseEntity.ok(studentService.getPendingHireOffers(authentication.getName()));
    }

    @PutMapping("/embauches/{applicationId}/accepter")
    public ResponseEntity<CandidatureDto> acceptHire(@PathVariable Long applicationId, Authentication authentication) {
        return ResponseEntity.ok(studentService.acceptHire(applicationId, authentication.getName()));
    }

    @PutMapping("/embauches/{applicationId}/decliner")
    public ResponseEntity<CandidatureDto> declineHire(@PathVariable Long applicationId, Authentication authentication) {
        return ResponseEntity.ok(studentService.declineHire(applicationId, authentication.getName()));
    }

    @GetMapping("/interviews")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<InterviewDto>> getInterviews(Authentication authentication) {
        return ResponseEntity.ok(studentService.getInterviews(authentication));
    }

    @PutMapping("/interviews/{id}/respond")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<InterviewDto> respondToInterview(@PathVariable Long id,
                                                           @RequestBody InterviewRespondRequest request,
                                                           Authentication authentication) {
        return ResponseEntity.ok(studentService.respondToInterview(id, request, authentication));
    }

}
