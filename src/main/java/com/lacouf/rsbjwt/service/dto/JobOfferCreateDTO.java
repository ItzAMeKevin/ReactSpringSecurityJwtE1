package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Programe;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class JobOfferCreateDTO extends GenericDto {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "Les prérequis sont obligatoires")
    private String prerequisites;

    @NotNull(message = "L'adresse est obligatoire")
    @Valid
    private AdresseDTO adresse;

    private String salary;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startingDate;

    @Positive
    private Integer durationInWeeks;

    @NotNull
    private Programe programe;
}
