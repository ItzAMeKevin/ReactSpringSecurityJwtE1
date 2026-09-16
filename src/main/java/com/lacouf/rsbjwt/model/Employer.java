package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;

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
    @Builder
    public Employer(
            Long id, String firstName, String lastName, String email, String password){
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.EMPLOYER).build());
    }
}
