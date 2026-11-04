package com.lacouf.rsbjwt.service.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.StudentCv;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CvMetaDataDto extends GenericDto {
    private Long id;
    private String fileName;
    private String contentType;
    private long size;
    private Status status;
    private Instant uploadedAt;



}
