package com.lacouf.rsbjwt.presentation;


import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employer")
public class EmployerController {
    private final EmployerService employerService;

    @PostMapping("/addJobOffer")
    public ResponseEntity<JobOfferDetailDTO> addJobOffrre(
            @Valid @RequestBody JobOfferCreateDTO jobOfferCreateDTO) {
        JobOfferDetailDTO created = employerService.addJobOffer(jobOfferCreateDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping()
    public List<JobOfferDetailDTO> getJobOffre(@RequestBody Long idEmployer){
        return employerService.getJobOffres(idEmployer);
    }


}
