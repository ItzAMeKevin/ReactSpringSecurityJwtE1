package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.SystemManager;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public class GestionnaireDto extends UserDTO {
    private String matricule;
    private String phoneNumber;;

    @Builder
    public GestionnaireDto(Long id, String firstName, String lastname,
                           String email, Role role, String matricule, String phoneNumber) {
        super(id, firstName, lastname, email, role);
        this.matricule = matricule;
        this.phoneNumber = phoneNumber;
    }

    public GestionnaireDto() {}

    public static GestionnaireDto create(SystemManager gestionnaire) {
        return GestionnaireDto.builder()
                .id(gestionnaire.getId())
                .firstName(gestionnaire.getFirstName())
                .lastname(gestionnaire.getLastName())
                .email(gestionnaire.getEmail())
                .role(gestionnaire.getRole())
                .matricule(gestionnaire.getMatricule())
                .phoneNumber(gestionnaire.getPhoneNumber())
                .build();
    }

    public static GestionnaireDto empty() {
        return new GestionnaireDto();
    }
}
