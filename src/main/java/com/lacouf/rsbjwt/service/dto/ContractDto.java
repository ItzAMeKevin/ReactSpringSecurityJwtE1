package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractDto extends GenericDto {
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
