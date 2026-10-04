package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;

public record AdresseDTO(
        @NotBlank String pay,
        @NotBlank String ville,
        @NotBlank String rue,
        @NotBlank String numeroCivic,
        @NotBlank String codePostal
) {}
