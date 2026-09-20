package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadCvDto {

    @NotBlank(message = "Le nom du fichier est obligatoire.")
    @Size(max = 255, message = "Le nom du fichier est trop long.")
    @Pattern(regexp = ".*\\.pdf", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Le fichier doit être un PDF.")
    private String fileName;

    @NotBlank(message = "Le type de fichier est requis.")
    private String contentType;

    @NotBlank(message = "Le contenu du fichier est requis.")
    @Size(max = 7_000_000, message = "Le contenu du fichier est trop volumineux.")
    private String content;
}


