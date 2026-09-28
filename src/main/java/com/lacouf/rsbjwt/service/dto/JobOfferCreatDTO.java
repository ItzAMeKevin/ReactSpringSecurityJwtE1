package com.lacouf.rsbjwt.service.dto;

import java.time.LocalDate;

public record JobOfferCreatDTO(
        String title,
        String description,
        String prerequisites,
        String location,
        String salary,
        LocalDate startingDate,
        Integer durationInWeeks,
        Long employerId
) {
}
