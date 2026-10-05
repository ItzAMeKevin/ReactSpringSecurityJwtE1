package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.Role;
import lombok.Builder;

public class ManagerCreateDto extends UserCreateDTO {

    @Builder
    public ManagerCreateDto(Long id, String firstName, String lastName,
                            String email, String password, Role role, String matricule) {
        super(firstName, lastName, email, password, role, matricule,
                null, null, null, null, null, null,null);
    }

    public ManagerCreateDto() {}

    public static ManagerCreateDto toManagerDto(Manager manager) {
        return ManagerCreateDto.builder()
                .id(manager.getId())
                .firstName(manager.getFirstName())
                .lastName(manager.getLastName())
                .email(manager.getEmail())
                .password(manager.getPassword())
                .matricule(manager.getMatricule())
                .role(manager.getRole())
                .build();
    }

    public static ManagerCreateDto empty() {
        return new ManagerCreateDto();
    }
}
