package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Adresse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AdresseDTO {


    @NotBlank(message = "Le pays est obligatoire")
    private String pays;


    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @NotBlank(message = "La rue est obligatoire")
    private String rue;

    @NotBlank(message = "Le numéro civique est obligatoire")
    private String numeroCivic;

    @NotBlank(message = "Le code postal est obligatoire")
    @Size(min = 6, max = 7, message = "Le code postal doit contenir 6 ou 7 caractères")
    @Pattern(
            regexp = "^[A-Za-z]\\d[A-Za-z][ ]?\\d[A-Za-z]\\d$",
            message = "Format de code postal canadien invalide (ex: H3B 1K9)"
    )
    private String codePostal;

}