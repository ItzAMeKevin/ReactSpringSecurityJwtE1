package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;
import jakarta.validation.constraints.NotNull;

public record UpdateCandidateStatusDto(
        @NotNull Status status
) {
}
