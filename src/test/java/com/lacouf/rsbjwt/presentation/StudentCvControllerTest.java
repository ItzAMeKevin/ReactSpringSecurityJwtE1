package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
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
    void handlesUploadListAndContentErrors() {
        when(authentication.getName()).thenReturn("student@test.com");
        CvUploadDto upload = new CvUploadDto("cv.pdf", "application/pdf", "content");
        doThrow(new InvalidCvException("invalid")).when(service).upload("student@test.com", upload);
        assertEquals(HttpStatus.BAD_REQUEST, controller.upload(authentication, upload).getStatusCode());
        CvUploadDto missing = new CvUploadDto("missing.pdf", "application/pdf", "content");
        doThrow(new UserNotFoundException()).when(service).upload("student@test.com", missing);
        assertEquals(HttpStatus.NOT_FOUND, controller.upload(authentication, missing).getStatusCode());

        when(service.listCvs(anyString())).thenThrow(new UserNotFoundException());
        assertEquals(HttpStatus.NOT_FOUND, controller.listCvs(authentication).getStatusCode());
        when(service.getCvContent(anyLong(), anyString())).thenThrow(new InvalidCvException("missing"));
        assertEquals(HttpStatus.NOT_FOUND, controller.getCvContent(1L, authentication).getStatusCode());
    }

    @Test
    void readsReviewAndSetsDefault() {
        when(authentication.getName()).thenReturn("student@test.com");
        when(service.getReviewFile(1L, "student@test.com")).thenReturn("feedback");
        assertEquals("feedback", controller.getReviewFile(1L, authentication).getBody());
        when(service.getReviewFile(2L, "student@test.com")).thenReturn(" ");
        assertEquals(HttpStatus.NOT_FOUND, controller.getReviewFile(2L, authentication).getStatusCode());
        when(service.getReviewFile(3L, "student@test.com")).thenThrow(new RuntimeException());
        assertEquals(HttpStatus.FORBIDDEN, controller.getReviewFile(3L, authentication).getStatusCode());

        doNothing().when(service).setDefaultCv(1L, "student@test.com");
        assertEquals(HttpStatus.OK, controller.setDefaultCv(1L, authentication).getStatusCode());
        doThrow(new UserNotFoundException()).when(service).setDefaultCv(2L, "student@test.com");
        assertEquals(HttpStatus.NOT_FOUND, controller.setDefaultCv(2L, authentication).getStatusCode());
        doThrow(new InvalidCvException("invalid")).when(service).setDefaultCv(3L, "student@test.com");
        assertEquals(HttpStatus.BAD_REQUEST, controller.setDefaultCv(3L, authentication).getStatusCode());
    }

    @Test
    void appliesOfferAndHandlesFailure() throws Exception {
        CvMetaDataDto cv = CvMetaDataDto.builder().id(1L).build();
        assertEquals(HttpStatus.OK, controller.appliquerOffre(2L, cv).getStatusCode());
        doThrow(new RuntimeException()).when(service).applyToJobOffer(3L, cv);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.appliquerOffre(3L, cv).getStatusCode());
    }
}
