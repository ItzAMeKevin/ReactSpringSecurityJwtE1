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
    @Column(unique = true, nullable = false)
    String NEQ;

    @Builder
    public Employer(
            Long id, String firstName, String lastName, String email, String password, String NEQ){
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.EMPLOYER).build());
        this.NEQ = NEQ;
    }


}
