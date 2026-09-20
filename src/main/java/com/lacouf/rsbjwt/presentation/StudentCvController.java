package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.StudentCvService;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import com.lacouf.rsbjwt.service.dto.UploadCvDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/student-cv")
public class StudentCvController {
    private final StudentCvService studentCvService;

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<CvMetaDataDto> upload(Authentication authentication, @Valid @RequestBody UploadCvDto request){
        CvMetaDataDto response = studentCvService.upload(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
    }
}
