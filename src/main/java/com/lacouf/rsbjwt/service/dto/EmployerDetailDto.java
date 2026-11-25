package com.lacouf.rsbjwt.service.dto;

public record EmployerDetailDto(
        Long id,
        String companyName,
        String email,
        String phoneNumber,
        AdresseDTO adresse
) {}
