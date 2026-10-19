package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.security.exception.NoValidatedCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/etudiant")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/offres")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<JobOfferDetailDTO>> getAcceptedOffers(Authentication authentication) {
       try{
           return ResponseEntity.ok(studentService.getAcceptedJobOffers(authentication.getName()));
       } catch (NoValidatedCvException e) {
              return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
       } catch (UserNotFoundException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
       }
    }
}
