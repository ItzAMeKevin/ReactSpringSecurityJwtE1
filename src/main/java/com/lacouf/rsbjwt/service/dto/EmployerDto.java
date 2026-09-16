package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public class EmployerDto extends UserDTO {
    
    @Builder
    public EmployerDto(Long id, String firstName, String lastname, String email, String password, Role role) {
        super(id, firstName, lastname, email, password, role, null);  
    }

    public EmployerDto() {}

    public static EmployerDto toEmployerDto(Employer employer) {
        return EmployerDto.builder()
                .id(employer.getId())
                .firstName(employer.getFirstName())
                .lastname(employer.getLastName())
                .email(employer.getEmail())
                .password(employer.getPassword())
                .role(employer.getRole())
                .build();
    }

    public static EmployerDto empty() {
        return new EmployerDto();
    }
}
