package com.lacouf.rsbjwt.presentation;


import com.lacouf.rsbjwt.service.EmployorService;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOffreDetailDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employer")
public class EmployerController {
    private final EmployorService employerService;

    @PostMapping("/addJoboffre")
    public ResponseEntity<JobOffreDetailDTO> addJobOffrre(
            @Valid @RequestBody JobOfferCreateDTO jobOfferCreateDTO) {
        JobOffreDetailDTO created = employerService.addJobOffer(jobOfferCreateDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

}
