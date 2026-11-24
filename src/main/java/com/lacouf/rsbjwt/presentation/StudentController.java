package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.service.dto.CandidateDto;

import java.util.List;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/embauches")
    public ResponseEntity<List<CandidatureDto>> get(Authentication authentication) {
        return ResponseEntity.ok(studentService.getHires(authentication.getName()));
    }

    @PutMapping("embauche/{id}/accepter")
    public ResponseEntity<CandidatureDto> acceptHire(@PathVariable Long id, @RequestBody CandidateDto candiatureDto, Authentication authentication) {
        CandidatureDto entity = studentService.acceptHire(id, candiatureDto, authentication.getName());
        return ResponseEntity.ok(entity);
    }
    
    @PutMapping("embauche/{id}/decliner")
    public ResponseEntity<CandidatureDto> declineHire(@PathVariable Long id, @RequestBody CandidateDto candiatureDto, Authentication authentication) {
        CandidatureDto entity = studentService.declineHire(id, candiatureDto, authentication.getName());
        return ResponseEntity.ok(entity);
    }
    
}