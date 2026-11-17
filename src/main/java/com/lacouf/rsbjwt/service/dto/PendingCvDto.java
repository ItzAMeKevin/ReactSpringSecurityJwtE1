package com.lacouf.rsbjwt.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PendingCvDto {
    private Long id;
    private String fileName;
    private Instant uploadedAt;
    private String studentFirstName;
    private String studentLastName;
    private String studentMatricule;
}
