package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CvDeclineFeedbackDto {

    @NotBlank(message = "errors.feedbackRequired")
    @Size(max = 2000, message = "errors.feedbackTooLong")
    private String feedback;
}
