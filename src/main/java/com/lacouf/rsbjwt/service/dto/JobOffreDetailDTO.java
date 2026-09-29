package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.OfferStatus;

import java.time.LocalDate;

public record JobOffreDetailDTO
  (
        Long id,
        String title,
        String description,
        String prerequisites,
        String location,
        String salary,
        OfferStatus status,
        LocalDate publicationDate,
        LocalDate startingDate,
        Integer durationInWeeks,
        Long employerId,
        String employerName
) {}
