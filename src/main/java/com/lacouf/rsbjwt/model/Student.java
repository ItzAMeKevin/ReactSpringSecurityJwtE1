package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@NoArgsConstructor
public class Student extends User {
    private String nom;
    @Column (unique = true, nullable = false)
    private String matricule;
    
    @Builder
    public Student(
            Long id, String firstName, String lastName, String email, String password,
            String matricule){
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.STUDENT).build());
        this.matricule = matricule;
    }
}
