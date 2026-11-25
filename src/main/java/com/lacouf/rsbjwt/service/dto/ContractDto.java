package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContractDto {
    private String erreur;
    private Long id;
    private byte[] content;
    private String fileName;
    private String contentType;
    private long size;
    private Status status;
    private Instant uploadedAt;
    @NotNull(message = "errors.studentIdRequired")
    private Long studentId;
    @NotNull(message = "errors.employerIdRequired")
    private Long employerId;
    @NotNull(message = "errors.managerIdRequired")
    private Long managerId;
}
