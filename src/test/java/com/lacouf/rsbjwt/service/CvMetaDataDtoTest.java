package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.CvStatus;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CvMetaDataDtoTest {
    @Test
    public void testCvMetaDataDto() {
        Instant date = Instant.now();
        StudentCv cv = new StudentCv();
        cv.setId(1L);
        cv.setFileName("test.pdf");
        cv.setContentType("application/pdf");
        cv.setSize(12345L);
        cv.setStatus(CvStatus.ACCEPTED);
        cv.setUploadedAt(date);

    CvMetaDataDto dto = CvMetaDataDto.toCvMetaDataDto(cv);
        assertEquals(1L, dto.getId());
        assertEquals("test.pdf", dto.getFileName());
        assertEquals("application/pdf", dto.getContentType());
        assertEquals(12345L, dto.getSize());
        assertEquals(CvStatus.ACCEPTED, dto.getStatus());
        assertEquals(date, dto.getUploadedAt());
    }
}
