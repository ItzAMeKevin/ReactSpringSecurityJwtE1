package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Programe;

import java.time.LocalDate;

public record JobOfferDetailDTO(
        Long id,
        String title,
        String description,
        String prerequisites,
        AdresseDTO location,
        String salary,
        Status status,
        LocalDate publicationDate,
        LocalDate startingDate,
        Integer durationInWeeks,
        Programe programe,
        String companyName
) {}
