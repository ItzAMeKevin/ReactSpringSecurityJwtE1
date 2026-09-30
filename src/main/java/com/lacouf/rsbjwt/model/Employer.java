package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.auth.Credentials;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("EMPLOYER")
@Getter @Setter
@NoArgsConstructor
@ToString(exclude = "jobOffers")
public class Employer extends User {

    @Embedded
    private Adresse adresse;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(unique = true , nullable = false)
    private String employerWorkId;


    @OneToMany(mappedBy = "employer")
    private List<JobOffer> jobOffers = new ArrayList<>();

    @Builder
    public Employer(
            Long id, String firstName, String lastName, String email, String password, String companyName,
            Adresse adresse, String phoneNumber, String employerWorkId) {
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.EMPLOYER).build());
        this.companyName = companyName;
        this.adresse = adresse;
        this.phoneNumber = phoneNumber;
        this.employerWorkId = employerWorkId;
    }
}
