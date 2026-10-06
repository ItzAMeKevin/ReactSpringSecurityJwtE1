package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@NoArgsConstructor
public class Student extends User {
    @Column (unique = true, nullable = false)
    private String matricule;
    @Column(nullable = false)
    private Programe programe;
    
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_manager_id", nullable = true)
    private Manager assignedManager;
    
    @Builder
    public Student(
            Long id, String firstName, String lastName, String email, String password,
            String matricule, Programe programe){
        super(id, firstName, lastName, Credentials.builder().email(email).password(password).role(Role.STUDENT).build());
        this.matricule = matricule;
        this.programe = programe;
    }
}

