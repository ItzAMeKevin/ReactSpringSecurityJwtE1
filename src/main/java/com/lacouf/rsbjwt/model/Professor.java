package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("P")
@Getter
@Setter
@NoArgsConstructor
public class Professor extends User {

    @Column (unique = true, nullable = false)
    private String matricule;
    
    @Builder
    public Professor(
            Long id, String firstName, String lastName, String email, String password,
            String matricule){
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.PREPOSE).build());
        this.matricule = matricule;
    }
}
