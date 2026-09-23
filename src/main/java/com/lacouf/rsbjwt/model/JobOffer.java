package com.lacouf.rsbjwt.model;


import com.lacouf.rsbjwt.model.auth.ContractType;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType contractType;

    @Column(nullable = false)
    private String location;

    private Double salary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferStatus status = OfferStatus.PUBLIEE;

    private LocalDate publicationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @Builder
    public JobOffer(String title, String description, String prerequisites,
                    ContractType contractType, String location, Double salary,
                    OfferStatus status, LocalDate publicationDate, Employer employer) {
        this.title = title;
        this.description = description;
        this.prerequisites = prerequisites;
        this.contractType = contractType;
        this.location = location;
        this.salary = salary;
        this.status = status != null ? status : OfferStatus.PUBLIEE;
        this.publicationDate = publicationDate;
        this.employer = employer;
    }
}