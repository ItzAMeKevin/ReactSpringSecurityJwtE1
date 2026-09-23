package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.Role;
import lombok.Builder;

public class ManagerDto extends UserDTO {

    @Builder
    public ManagerDto(Long id, String firstName, String lastname,
                           String email, String password, Role role, String matricule) {
        super(id, firstName, lastname, email, password, role, matricule);
    }

    public ManagerDto() {}

    public static ManagerDto toManagerDto(Manager manager) {
        return ManagerDto.builder()
                .id(manager.getId())
                .firstName(manager.getFirstName())
                .lastname(manager.getLastName())
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
