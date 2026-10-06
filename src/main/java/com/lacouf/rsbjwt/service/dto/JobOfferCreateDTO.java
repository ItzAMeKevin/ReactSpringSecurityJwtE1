package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record JobOfferCreateDTO(
        @NotBlank (message = "Le titre est obligatoire")
        String title,
        @NotBlank(message = "La description est obligatoire")
        String description,
        @NotBlank (message = "Les prérequis sont obligatoires")
        String prerequisites,
        @NotNull (message = "L'adresse est obligatoire")
        @Valid AdresseDTO adresse,
        String salary,
        @NotNull(message = "La date de début est obligatoire")
        LocalDate startingDate,
        @Positive Integer durationInWeeks,
        @NotNull Long employer_id
) {
}
