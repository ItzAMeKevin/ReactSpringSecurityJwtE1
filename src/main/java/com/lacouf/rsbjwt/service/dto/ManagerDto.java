package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public class ManagerDto extends UserDTO {

    @Builder
    public ManagerDto(Long id, String firstName, String lastname,
                           String email, Role role, String matricule) {
        super(id, firstName, lastname, email, role, matricule);
    }

    public ManagerDto() {}

    public static ManagerDto toManagerDto(Manager manager) {
        return ManagerDto.builder()
                .id(manager.getId())
                .firstName(manager.getFirstName())
                .lastname(manager.getLastName())
                .email(manager.getEmail())
                .role(manager.getRole())
                .build();
    }

    public static ManagerDto empty() {
        return new ManagerDto();
    }
}
