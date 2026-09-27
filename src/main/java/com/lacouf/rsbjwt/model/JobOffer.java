package com.lacouf.rsbjwt.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false, length = 2000)
    private String prerequisites;


    @Column(nullable = false)
    private String location;

    private String salary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferStatus status = OfferStatus.ON_WAIT;

    private LocalDate publicationDate;
    private LocalDate startingDate;
    private int durationInWeeks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @Builder
    public JobOffer(String title, String description, String prerequisites,
                    String location, String salary,
                    OfferStatus status, LocalDate startingDate, int durationInWeeks, Employer employer) {
        this.title = title;
        this.description = description;
        this.prerequisites = prerequisites;
        this.location = location;
        this.salary = salary;
        this.status = status != null ? status : OfferStatus.ON_WAIT;
        this.publicationDate = LocalDate.now();
        this.startingDate = startingDate;
        this.durationInWeeks = durationInWeeks;
        this.employer = employer;
    }
}