package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.JobOffer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record JobOfferCreateDTO(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String prerequisites,
        @NotBlank String location,
        String salary,
        LocalDate startingDate,
        @Positive Integer durationInWeeks,
        @NotNull Long employerId
) {
}
