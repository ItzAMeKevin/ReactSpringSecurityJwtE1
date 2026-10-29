package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;

import java.time.Instant;

public record CandidateDto(
        Long cvId,
        String firstName,
        String lastName,
        String email,
        Instant appliedAt,
        Status status
) {
}
