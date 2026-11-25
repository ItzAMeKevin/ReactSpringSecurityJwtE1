package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.InvalidCvException;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.StudentCvService;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentCvControllerTest {

    @Mock private StudentCvService service;
    @Mock private Authentication authentication;
    private StudentCvController controller;

    @BeforeEach
    void setUp() {
        controller = new StudentCvController(service);
    }

    @Test
    void uploadsListsAndReadsCv() {
        when(authentication.getName()).thenReturn("student@test.com");
        CvUploadDto upload = new CvUploadDto("cv.pdf", "application/pdf", "content");
        CvMetaDataDto metadata = CvMetaDataDto.builder().id(1L).build();
        when(service.upload("student@test.com", upload)).thenReturn(metadata);
        when(service.listCvs("student@test.com")).thenReturn(List.of(metadata));
        when(service.getCvContent(1L, "student@test.com")).thenReturn(new byte[]{1});

        assertEquals(HttpStatus.CREATED, controller.upload(authentication, upload).getStatusCode());
        assertEquals(List.of(metadata), controller.listCvs(authentication).getBody());
        assertArrayEquals(new byte[]{1}, controller.getCvContent(1L, authentication).getBody());
    }

    @Test
    void propagatesServiceExceptions() {
        when(authentication.getName()).thenReturn("student@test.com");
        CvUploadDto upload = new CvUploadDto("cv.pdf", "application/pdf", "content");
        doThrow(new InvalidCvException("errors.invalidPdf")).when(service).upload("student@test.com", upload);
        assertThrows(InvalidCvException.class, () -> controller.upload(authentication, upload));

        when(service.listCvs(anyString())).thenThrow(new UserNotFoundException());
        assertThrows(UserNotFoundException.class, () -> controller.listCvs(authentication));

        when(service.getCvContent(anyLong(), anyString())).thenThrow(new CvException("errors.cvNotFound"));
        assertThrows(CvException.class, () -> controller.getCvContent(1L, authentication));
    }



    @Test
    void appliesOfferAndHandlesFailure() throws Exception {
        CvMetaDataDto cv = CvMetaDataDto.builder().id(1L).build();
        assertEquals(HttpStatus.OK, controller.appliquerOffre(2L, cv).getStatusCode());
        doThrow(new RuntimeException()).when(service).applyToJobOffer(3L, cv);
        assertThrows(RuntimeException.class, () -> controller.appliquerOffre(3L, cv));
    }
}
