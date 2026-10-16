package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvDeclineFeedbackDto {

    @NotBlank(message = "Le feedback est obligatoire.")
    @Size(max = 2000, message = "Le feedback est trop long.")
    private String feedback;
}
