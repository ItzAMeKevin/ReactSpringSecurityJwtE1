package com.lacouf.rsbjwt.service.dto;

public record PendingInternshipSiteDto(
        Long id,
        String studentFirstName,
        String studentLastName,
        String companyName,
        String startDate
) {}
