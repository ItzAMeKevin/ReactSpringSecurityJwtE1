package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.InvalidCvException;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.StudentCvService;
import com.lacouf.rsbjwt.service.StudentService;
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
class StudentControllerCvTest {

    @Mock private StudentCvService cvService;
    @Mock private StudentService studentService;
    @Mock private Authentication authentication;
    private StudentController controller;

    @BeforeEach
    void setUp() {
        controller = new StudentController(cvService, studentService);
    }

    @Test
    void uploadsListsAndReadsCv() {
        when(authentication.getName()).thenReturn("student@test.com");
        CvUploadDto upload = new CvUploadDto("cv.pdf", "application/pdf", "content");
        CvMetaDataDto metadata = CvMetaDataDto.builder().id(1L).build();
        when(cvService.upload("student@test.com", upload)).thenReturn(metadata);
        when(cvService.listCvs("student@test.com")).thenReturn(List.of(metadata));
        when(cvService.getCvContent(1L, "student@test.com")).thenReturn(new byte[]{1});

        assertEquals(HttpStatus.CREATED, controller.upload(authentication, upload).getStatusCode());
        assertEquals(List.of(metadata), controller.listCvs(authentication).getBody());
        assertArrayEquals(new byte[]{1}, controller.getCvContent(1L, authentication).getBody());
    }

    @Test
    void propagatesServiceExceptions() {
        when(authentication.getName()).thenReturn("student@test.com");
        CvUploadDto upload = new CvUploadDto("cv.pdf", "application/pdf", "content");
        doThrow(new InvalidCvException("errors.invalidPdf")).when(cvService).upload("student@test.com", upload);
        assertThrows(InvalidCvException.class, () -> controller.upload(authentication, upload));

        when(cvService.listCvs(anyString())).thenThrow(new UserNotFoundException());
        assertThrows(UserNotFoundException.class, () -> controller.listCvs(authentication));

        when(cvService.getCvContent(anyLong(), anyString())).thenThrow(new CvException("errors.cvNotFound"));
        assertThrows(CvException.class, () -> controller.getCvContent(1L, authentication));
    }

    @Test
    void appliesOfferAndHandlesFailure() throws Exception {
        CvMetaDataDto cv = CvMetaDataDto.builder().id(1L).build();
        assertEquals(HttpStatus.OK, controller.appliquerOffre(2L, cv).getStatusCode());
        doThrow(new RuntimeException()).when(cvService).applyToJobOffer(3L, cv);
        assertThrows(RuntimeException.class, () -> controller.appliquerOffre(3L, cv));
    }
}
