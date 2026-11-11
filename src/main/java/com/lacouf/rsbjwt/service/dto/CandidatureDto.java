package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;

import java.time.Instant;

public record CandidatureDto(
        Long id,
        StudentInfoDto student,
        JobOfferInfoDto jobOffer,
        Status status,
        Instant createdAt,
        Instant updatedAt
) {
    public record StudentInfoDto(
            String firstName,
            String lastName,
            String matricule,
            String email
    ) {}

    public record JobOfferInfoDto(
            String title,
            String companyName,
            int durationInWeeks
    ) {}
}
