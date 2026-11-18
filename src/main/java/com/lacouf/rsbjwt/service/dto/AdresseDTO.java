package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class AdresseDTO {

    @NotBlank(message = "errors.countryRequired")
    private String pays;

    @NotBlank(message = "errors.cityRequired")
    private String ville;

    @NotBlank(message = "errors.streetRequired")
    private String rue;

    @NotBlank(message = "errors.civicNumberRequired")
    private String numeroCivic;

    @NotBlank(message = "errors.postalCodeRequired")
    @Size(min = 6, max = 7, message = "errors.postalCodeLength")
    @Pattern(
            regexp = "^[A-Za-z]\\d[A-Za-z][ ]?\\d[A-Za-z]\\d$",
            message = "errors.postalCodeFormat"
    )
    private String codePostal;

}
