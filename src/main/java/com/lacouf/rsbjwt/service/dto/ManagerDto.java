package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public class ManagerDto extends UserDTO {

    @Builder
    public ManagerDto(Long id, String firstName, String lastName,
                           String email, String password, Role role, String matricule) {
        super(id, firstName, lastName, email, password, role, matricule, null, null, null, null, null, null);
    }

    public ManagerDto() {}

    public static ManagerDto toManagerDto(Manager manager) {
        return ManagerDto.builder()
                .id(manager.getId())
                .firstName(manager.getFirstName())
                .lastName(manager.getLastName())
                .email(manager.getEmail())
                .password(manager.getPassword())
                .matricule(manager.getMatricule())
                .role(manager.getRole())
                .build();
    }

    public static ManagerDto empty() {
        return new ManagerDto();
    }
}
