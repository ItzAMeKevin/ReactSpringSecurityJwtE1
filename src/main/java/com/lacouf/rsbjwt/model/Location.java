package com.lacouf.rsbjwt.model;



import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pays;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String ville;

    @Column(nullable = false)
    private String rue;

    @Column(nullable = false)
    private String numeroCivic;

    @Column(nullable = false)
    private String codePostal;

    @Builder
    public Location(String pays, String province, String ville,
                    String rue, String numeroCivic, String codePostal) {
        this.pays = pays;
        this.province = province;
        this.ville = ville;
        this.rue = rue;
        this.numeroCivic = numeroCivic;
        this.codePostal = codePostal;
    }
}