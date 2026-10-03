package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("/offres")
    public ResponseEntity<JobOfferDto> submitOffer(@RequestBody JobOfferCreateDto dto, Authentication authentication) {
        String email = authentication.getName();
        return employerService.submitOffer(email, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
