package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.StudentCvService;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StudentCvControllerTest {

    @Mock
    private StudentCvService studentCvService;

    @InjectMocks
    private StudentCvController studentCvController;

    @Test
    void testAppliquerOffreReturnsSuccess() throws Exception {
        CvMetaDataDto cvDto = CvMetaDataDto.builder().id(5L).build();

        ResponseEntity<String> response = studentCvController.appliquerOffre(10L, cvDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Candidature soumise avec succès.", response.getBody());
        verify(studentCvService).applyToJobOffer(10L, cvDto);
    }

    @Test
    void testAppliquerOffreReturnsInternalServerErrorWhenApplicationFails() throws Exception {
        CvMetaDataDto cvDto = CvMetaDataDto.builder().id(5L).build();
        doThrow(new RuntimeException("application failed"))
                .when(studentCvService).applyToJobOffer(10L, cvDto);

        ResponseEntity<String> response = studentCvController.appliquerOffre(10L, cvDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Une erreur est survenue lors de la candidature.", response.getBody());
    }
}
