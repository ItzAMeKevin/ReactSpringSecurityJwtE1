package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;

import lombok.*;
import lombok.experimental.SuperBuilder;

import com.lacouf.rsbjwt.model.Programe;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class JobOfferDetailDTO extends GenericDto {

    private Long id;
    private String title;
    private String description;
    private String prerequisites;
    private AdresseDTO location;
    private String salary;
    private Status status;
    private LocalDate publicationDate;
    private LocalDate startingDate;
    private Integer durationInWeeks;
    private Programe programe;
    private String companyName;
    private boolean applied;
    private Status applicationStatus;
    private String refusalFeedback;

    public JobOfferDetailDTO(Long id, String title, String description, String prerequisites,
                             AdresseDTO location, String salary, Status status,
                             LocalDate publicationDate, LocalDate startingDate,
                             Integer durationInWeeks, Programe programe, String companyName) {
        this(id, title, description, prerequisites, location, salary, status,
                publicationDate, startingDate, durationInWeeks, programe, companyName, false, null, null);
    }

    public JobOfferDetailDTO withApplication(boolean applied, Status applicationStatus) {
        JobOfferDetailDTO copy = new JobOfferDetailDTO(id, title, description, prerequisites, location, salary,
                status, publicationDate, startingDate, durationInWeeks, programe,
                companyName, applied, applicationStatus, refusalFeedback);
        copy.setErreur(getErreur());
        return copy;
    }
}
