package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LocationCreateDTO(

        @NotBlank(message = "Le pays est obligatoire")
        String pays,

        @NotBlank(message = "La province est obligatoire")
        String province,

        @NotBlank(message = "La ville est obligatoire")
        String ville,

        @NotBlank(message = "La rue est obligatoire")
        String rue,

        @NotBlank(message = "Le numéro civique est obligatoire")
        String numeroCivic,

        @NotBlank(message = "Le code postal est obligatoire")
        @Size(min = 6, max = 7, message = "Le code postal doit contenir 6 ou 7 caractères")
        @Pattern(
                regexp = "^[A-Za-z]\\d[A-Za-z][ ]?\\d[A-Za-z]\\d$",
                message = "Format de code postal canadien invalide (ex: H3B 1K9)"
        )
        String codePostal
) {}