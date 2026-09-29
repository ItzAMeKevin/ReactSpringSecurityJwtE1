package com.lacouf.rsbjwt.service.dto;


import com.lacouf.rsbjwt.model.CvStatus;
import com.lacouf.rsbjwt.model.StudentCv;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CvMetaDataDto {
    private Long id;
    private String fileName;
    private String contentType;
    private long size;
    private CvStatus status;
    private Instant uploadedAt;

    public static CvMetaDataDto toCvMetaDataDto(StudentCv cv){
        return CvMetaDataDto.builder()
                .id(cv.getId())
                .fileName(cv.getFileName())
                .contentType(cv.getContentType())
                .size(cv.getSize())
                .status(cv.getStatus())
                .uploadedAt(cv.getUploadedAt())
                .build();
    }
}
