package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;

import java.time.Instant;

public record JobApplicationSuiviDto(
        Long id,
        Status status,
        Instant appliedAt,
        Instant updatedAt,
        StudentDetailsDto student,
        JobOfferDetailDTO jobOffer,
        EmployerDetailDto employer
) {}
