package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferNotFoundException;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionnaireControllerWebMvcTest {

    @Mock
    private GestionnaireService service;
    private GestionnaireController controller;

    @BeforeEach
    void setUp() {
        controller = new GestionnaireController(service, mock(com.lacouf.rsbjwt.service.SystemeService.class),
                mock(com.lacouf.rsbjwt.service.StudentCvService.class));
    }

    @Test
    void getsPendingCvsAndOffers() {
        PendingCvDto cv = PendingCvDto.builder().id(1L).build();
        JobOfferDto offer = JobOfferDto.builder().id(2L).build();
        when(service.getPendingCvs()).thenReturn(List.of(cv));
        when(service.getPendingOffers()).thenReturn(List.of(offer));

        assertEquals(List.of(cv), controller.getPendingCvs().getBody());
        assertEquals(List.of(offer), controller.getPendingOffers().getBody());
    }

    @Test
    void getsCvContent() {
        when(service.getCvContent(1L)).thenReturn(new byte[]{1});
        assertEquals(HttpStatus.OK, controller.getCvContent(1L).getStatusCode());

        when(service.getCvContent(2L)).thenThrow(new UserNotFoundException());
        assertEquals(HttpStatus.NOT_FOUND, controller.getCvContent(2L).getStatusCode());
    }

    @Test
    void acceptsAndDeclinesCv() {
        PendingCvDto accepted = PendingCvDto.builder().id(1L).build();
        when(service.acceptCv(1L)).thenReturn(accepted);
        assertEquals(accepted, controller.acceptCv(1L).getBody());

        when(service.acceptCv(2L)).thenThrow(new UserNotFoundException());
        assertEquals(HttpStatus.BAD_REQUEST, controller.acceptCv(2L).getStatusCode());

        CvDeclineFeedbackDto feedback = new CvDeclineFeedbackDto("feedback");
        doNothing().when(service).declineCv(1L, feedback);
        assertEquals(feedback, controller.declineCv(1L, feedback).getBody());

        doThrow(new UserNotFoundException()).when(service).declineCv(2L, feedback);
        assertEquals(HttpStatus.BAD_REQUEST, controller.declineCv(2L, feedback).getStatusCode());
    }

    @Test
    void acceptsAndRefusesOffers() throws Throwable {
        JobOfferDto offer = JobOfferDto.builder().id(1L).build();
        when(service.acceptOffer(1L)).thenReturn(offer);
        when(service.refuseOffer(1L)).thenReturn(offer);
        assertEquals(offer, controller.acceptOffer(1L).getBody());
        assertEquals(offer, controller.refuseOffer(1L).getBody());

        when(service.acceptOffer(2L)).thenThrow(new JobOfferNotFoundException("missing"));
        assertEquals(HttpStatus.BAD_REQUEST, controller.acceptOffer(2L).getStatusCode());
        when(service.refuseOffer(2L)).thenThrow(new InvalidStateTransitionException("invalid"));
        assertEquals(HttpStatus.BAD_REQUEST, controller.refuseOffer(2L).getStatusCode());
    }
}
