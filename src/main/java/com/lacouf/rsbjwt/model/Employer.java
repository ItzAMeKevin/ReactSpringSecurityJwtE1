package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("EMPLOYER")
@Getter
@NoArgsConstructor
public class Employer extends User {

    @Column
    private String companyName;
    @Column
    private String address;
    @Column
    private String postalCode;
    @Column
    private String city;
    @Column
    private String phoneNumber;
    @Column(unique = true)
    private String employerId;

    @Builder
    public Employer(
            Long id, String firstName, String lastName, String email, String password, String companyName,
            String address, String postalCode, String city, String phoneNumber, String employerId) {
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.EMPLOYER).build());
        this.companyName = companyName;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.employerId = employerId;
    }
}
