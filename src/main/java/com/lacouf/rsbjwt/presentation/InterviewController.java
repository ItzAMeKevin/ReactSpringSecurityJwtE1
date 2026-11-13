package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.service.InterviewService;
import com.lacouf.rsbjwt.service.dto.InterviewResponseDto;
import com.lacouf.rsbjwt.service.dto.ScheduleInterviewRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/schedule")
    public ResponseEntity<InterviewResponseDto> schedule(
            @Valid @RequestBody ScheduleInterviewRequestDto dto,
            Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(interviewService.schedule(dto, authentication.getName()));
        } catch (ApplicationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
