package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Adresse {

    @Column
    private String pay;

    @Column(nullable = false)
    private String ville;

    @Column(nullable = false)
    private String rue;

    @Column(nullable = false)
    private String numeroCivic;

    @Column(nullable = false)
    private String codePostal;

    @Builder
    public Adresse(String pays, String ville, String rue, String numeroCivic, String codePostal) {
        this.pay = pays;
        this.ville = ville;
        this.rue = rue;
        this.numeroCivic = numeroCivic;
        this.codePostal = codePostal;
    }
}
