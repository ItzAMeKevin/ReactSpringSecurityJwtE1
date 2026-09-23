package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LocationDTO {

    private Long id;

    @NotBlank(message = "Le pays est obligatoire")
    private String pays;

    @NotBlank(message = "La province est obligatoire")
    private String province;

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

    public static LocationDTO fromEntity(Location l) {
        if (l == null) return null;
        return LocationDTO.builder()
                .id(l.getId())
                .pays(l.getPays())
                .province(l.getProvince())
                .ville(l.getVille())
                .rue(l.getRue())
                .numeroCivic(l.getNumeroCivic())
                .codePostal(l.getCodePostal())
                .build();
    }
}