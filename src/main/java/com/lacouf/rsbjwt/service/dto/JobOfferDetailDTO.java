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
        String companyName,
        boolean applied,
        Status applicationStatus
) {
    public JobOfferDetailDTO(Long id, String title, String description, String prerequisites,
                              AdresseDTO location, String salary, Status status,
                              LocalDate publicationDate, LocalDate startingDate,
                              Integer durationInWeeks, Programe programe, String companyName) {
        this(id, title, description, prerequisites, location, salary, status,
                publicationDate, startingDate, durationInWeeks, programe, companyName, false, null);
    }
    public JobOfferDetailDTO withApplied(boolean applied) {
        return new JobOfferDetailDTO(id, title, description, prerequisites, location, salary,
                status, publicationDate, startingDate, durationInWeeks, programe,
                companyName, applied, applicationStatus);
    }
    public JobOfferDetailDTO withApplicationStatus(Status applicationStatus) {
        return new JobOfferDetailDTO(id, title, description, prerequisites, location, salary,
                status, publicationDate, startingDate, durationInWeeks, programe,
                companyName, applied, applicationStatus);
    }
}
