package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Programe;

import java.time.LocalDate;

public record JobOfferCreateDto(
        String title,
        String description,
        String prerequisites,
        AdresseDTO adresse,
        String salary,
        LocalDate startingDate,
        int durationInWeeks,
        Programe programe
) {}
