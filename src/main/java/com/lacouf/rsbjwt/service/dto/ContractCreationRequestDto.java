package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractCreationRequestDto {
    @NotNull(message = "errors.studentIdRequired")
    private Long studentId;

    @NotNull(message = "errors.employerIdRequired")
    private Long employerId;

    @NotNull(message = "errors.managerIdRequired")
    private Long managerId;

    @NotNull(message = "errors.jobOfferIdRequired")
    private Long jobOfferId;
}
