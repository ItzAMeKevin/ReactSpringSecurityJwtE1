package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Programe;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record JobOfferCreateDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String prerequisites,
        @Valid @NotNull AdresseDTO adresse,
        @NotBlank String salary,
        @NotNull LocalDate startingDate,
        @Min(1) int durationInWeeks,
        @NotNull Programe programe
) {}
