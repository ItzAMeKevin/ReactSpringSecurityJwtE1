package com.lacouf.rsbjwt.service.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class JobOfferDto {

    private Long id;
    private String title;
    private String description;
    private String prerequisites;
    private AdresseDTO adresse;
    private String salary;
    private LocalDate startingDate;
    private int durationInWeeks;
    private String companyName;
    private LocalDate publicationDate;
    private boolean applied;
}
