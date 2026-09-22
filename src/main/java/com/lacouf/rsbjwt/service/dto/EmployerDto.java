package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;

public class EmployerDto extends UserDTO {

    @Builder
    public EmployerDto(Long id,
                       String firstName,
                       String lastName,
                       String email,
                       String password,
                       Role role,
                       String companyName,
                       String address,
                       String postalCode,
                       String city,
                       String phoneNumber,
                       String employerId) {
        super(id,
                firstName,
                lastName,
                email,
                password,
                role,
                null,
                companyName,
                address,
                postalCode,
                city,
                phoneNumber,
                employerId);
    }

    public EmployerDto() {}

    public static EmployerDto toEmployerDto(Employer employer) {
        return EmployerDto.builder()
                .id(employer.getId())
                .firstName(employer.getFirstName())
                .lastName(employer.getLastName())
                .email(employer.getEmail())
                .password(employer.getPassword())
                .role(employer.getRole())
                .companyName(employer.getCompanyName())
                .address(employer.getAddress())
                .postalCode(employer.getPostalCode())
                .city(employer.getCity())
                .phoneNumber(employer.getPhoneNumber())
                .employerId(employer.getEmployerId())
                .build();
    }

    public static EmployerDto empty() {
        return new EmployerDto();
    }
}