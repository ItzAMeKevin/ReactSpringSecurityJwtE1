package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {
    @NotBlank(message = "errors.feedbackRequired")
    @Size(max = 2000, message = "errors.feedbackTooLong")
    private String feedback;
}
