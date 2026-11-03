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
}
