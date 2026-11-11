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
public class JobOfferCreateDTO {

    @NotBlank(message = "errors.titleRequired")
    private String title;

    @NotBlank(message = "errors.descriptionRequired")
    private String description;

    @NotBlank(message = "errors.prerequisitesRequired")
    private String prerequisites;

    @NotNull(message = "errors.addressRequired")
    @Valid
    private AdresseDTO adresse;

    private String salary;

    @NotNull(message = "errors.startingDateRequired")
    private LocalDate startingDate;

    @Positive
    private Integer durationInWeeks;

    @NotNull
    private Programe programe;
}
