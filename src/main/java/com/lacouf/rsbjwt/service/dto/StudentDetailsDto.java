package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Programe;

public record StudentDetailsDto(
        Long id,
        String firstName,
        String lastName,
        String matricule,
        String email,
        Programe programe
) {}
