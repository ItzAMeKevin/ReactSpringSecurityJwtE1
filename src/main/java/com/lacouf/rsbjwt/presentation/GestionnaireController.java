package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@RestController
@RequestMapping("/gestionnaire")
@RequiredArgsConstructor
public class GestionnaireController {

    private final GestionnaireService gestionnaireService;
    private final HandlerMapping resourceHandlerMapping;

    @GetMapping("/cv/pending")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<PendingCvDto>> getpendingCvs() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(gestionnaireService.getPendingCvs());
    }

    @GetMapping("/cv/{cvId}/content")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<byte[]> getCvContent(@PathVariable Long cvId) {
        try {
            byte[] content = gestionnaireService.getCvContent(cvId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cv/{cvId}/accept")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> acceptCv(@PathVariable Long cvId) {
        try {
            gestionnaireService.acceptCv(cvId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cv/{cvId}/decline")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> declineCv(@PathVariable Long cvId, @Valid @RequestBody CvUploadDto reviewRequest) {
        try {
            gestionnaireService.declineCv(cvId, reviewRequest);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
