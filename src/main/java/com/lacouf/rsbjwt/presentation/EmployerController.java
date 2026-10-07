package com.lacouf.rsbjwt.presentation;


import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        try {
            return employerService.updateRefusedJobOffer(id, jobOfferCreateDTO, authentication.getName())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
