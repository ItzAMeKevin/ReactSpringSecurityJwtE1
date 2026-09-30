package com.lacouf.rsbjwt.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String prerequisites;



    private String salary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate publicationDate;

    @Column(nullable = false)
    private LocalDate startingDate;
    @Column(nullable = false)
    private int durationInWeeks;

    @ManyToOne(fetch = FetchType.LAZY ,optional = false)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;


    @ManyToOne(fetch = FetchType.LAZY ,optional = false)
    @JoinColumn(nullable = false)
    private Location location;

    @Builder
    public JobOffer(String title, String description, String prerequisites,
                    Location location, String salary,
                     LocalDate startingDate, int durationInWeeks, Employer employer) {
        this.title = title;
        this.description = description;
        this.prerequisites = prerequisites;
        this.location = location;
        this.salary = salary;
        this.startingDate = startingDate;
        this.durationInWeeks = durationInWeeks;
        this.employer = employer;
    }

    @PrePersist
    void onCreate() {
        if (publicationDate == null) {
            publicationDate = LocalDate.now();
        }
        if (status == null) {
            status = OfferStatus.WAITING;
        }
    }
}