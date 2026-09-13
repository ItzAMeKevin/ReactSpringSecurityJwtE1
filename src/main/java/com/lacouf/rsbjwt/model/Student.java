package com.lacouf.rsbjwt.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@DiscriminatorValue("SM")
@Getter
@Setter
@NoArgsConstructor
public class Student extends User {
    private String nom;
}
