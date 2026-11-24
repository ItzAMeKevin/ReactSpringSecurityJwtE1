package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.InterviewType;
import com.lacouf.rsbjwt.model.Status;

import java.time.Instant;
import java.util.List;

public record InterviewDto(
        Long id,
        Long candidatureId,
        String companyName,
        String offerTitle,
        InterviewType type,
        String videoLink,
        AdresseDTO address,
        String notes,
        Instant scheduledAt,
        List<InterviewSlotDto> slots,
        Status status,
        String studentMessage,
        boolean expired
) {}
