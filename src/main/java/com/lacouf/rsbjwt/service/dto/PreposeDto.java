package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Professor;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public class PreposeDto extends UserDTO {
    
    @Builder
    public PreposeDto(Long id, String firstName, String lastname, String email, Role role) {
        super(id, firstName, lastname, email, role);
    }

    public PreposeDto() {}

    public static PreposeDto create(Professor prepose) {
        return PreposeDto.builder()
                .id(prepose.getId())
                .firstName(prepose.getFirstName())
                .lastname(prepose.getLastName())
                .email(prepose.getEmail())
                .role(prepose.getRole())
                .build();
    }

    public static PreposeDto empty() {
        return new PreposeDto();
    }
}
