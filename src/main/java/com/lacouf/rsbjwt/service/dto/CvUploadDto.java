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
public class CvUploadDto extends GenericDto {

    @NotBlank(message = "errors.fileNameRequired")
    @Size(max = 255, message = "errors.fileNameTooLong")
    private String fileName;

    @NotBlank(message = "errors.fileTypeRequired")
    private String contentType;

    @NotBlank(message = "errors.fileContentRequired")
    @Size(max = 7_000_000, message = "errors.fileContentTooLarge")
    private String content;
}

