package com.lacouf.rsbjwt.service.dto;

import java.time.LocalDate;

public record JobOfferDto(
        Long id,
        String title,
        String description,
        String prerequisites,
        AdresseDTO adresse,
        String salary,
        LocalDate startingDate,
        int durationInWeeks,
        String companyName,
        LocalDate publicationDate,
        boolean applied
) {
    public JobOfferDto(Long id, String title, String description, String prerequisites,
                       AdresseDTO adresse, String salary, LocalDate startingDate,
                       int durationInWeeks, String companyName, LocalDate publicationDate) {
        this(id, title, description, prerequisites, adresse, salary, startingDate,
                durationInWeeks, companyName, publicationDate, false);
    }
}
