package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCandidateStatusDto {

    @NotNull
    private Status status;
}
