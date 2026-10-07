package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("EMPLOYER")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "jobOffers")
public class Employer extends User {

    @Embedded
    private Adresse adresse;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(unique = true, nullable = false)
    private String employerWorkId;

    @OneToMany(mappedBy = "employer")
    private List<JobOffer> jobOffers = new ArrayList<>();


}
