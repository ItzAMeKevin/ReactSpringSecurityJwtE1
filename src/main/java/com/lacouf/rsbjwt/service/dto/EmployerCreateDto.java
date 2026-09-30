package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.Role;
import lombok.Builder;

public class EmployerCreateDto extends UserCreateDTO {

    @Builder
    public EmployerCreateDto(Long id,
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
                employerId,null);
    }

    public EmployerCreateDto() {}

    public static EmployerCreateDto toEmployerDto(Employer employer) {
        return EmployerCreateDto.builder()
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
                .employerId(employer.getEmployerWorkId())
                .build();
    }

    public static EmployerCreateDto empty() {
        return new EmployerCreateDto();
    }
}