package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.OfferStatus;

import java.time.LocalDate;

public record JobOfferDetailDTO
  (
        Long id,
        String title,
        String description,
        String prerequisites,
        AdresseDTO location,
        String salary,
        OfferStatus status,
        LocalDate publicationDate,
        LocalDate startingDate,
        Integer durationInWeeks,
        String companyName

) {}
