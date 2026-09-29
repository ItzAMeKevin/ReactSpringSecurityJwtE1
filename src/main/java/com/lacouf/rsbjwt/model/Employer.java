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

    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String postalCode;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(unique = true , nullable = false)
    private String employerWorkId;


    @OneToMany(mappedBy = "employer")
    private List<JobOffer> jobOffers = new ArrayList<>();

    @Builder
    public Employer(
            Long id, String firstName, String lastName, String email, String password, String companyName,
            String address, String postalCode, String city, String phoneNumber, String employerWorkId) {
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.EMPLOYER).build());
        this.companyName = companyName;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.employerWorkId = employerWorkId;
    }
}
