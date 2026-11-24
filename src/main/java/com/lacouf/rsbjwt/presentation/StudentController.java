package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/etudiant")
@PreAuthorize("hasAuthority('STUDENT')")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/offres")
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
}
