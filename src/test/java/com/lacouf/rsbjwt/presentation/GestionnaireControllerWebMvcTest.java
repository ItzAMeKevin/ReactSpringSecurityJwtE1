package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferNotFoundException;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.*;
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
        controller = new GestionnaireController(service);
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

        when(service.getCvContent(2L)).thenThrow(new CvException("errors.cvNotFound"));
        assertThrows(CvException.class, () -> controller.getCvContent(2L));
    }

    @Test
    void acceptsAndDeclinesCv() {
        PendingCvDto accepted = PendingCvDto.builder().id(1L).build();
        when(service.acceptCv(1L)).thenReturn(accepted);
        assertEquals(accepted, controller.acceptCv(1L).getBody());

        when(service.acceptCv(2L)).thenThrow(new CvException("errors.cvNotFound"));
        assertThrows(CvException.class, () -> controller.acceptCv(2L));

        FeedbackDto feedback = new FeedbackDto("feedback");
        doNothing().when(service).declineCv(1L, feedback);
        assertEquals(feedback, controller.declineCv(1L, feedback).getBody());

        doThrow(new CvException("errors.cvNotFound")).when(service).declineCv(2L, feedback);
        assertThrows(CvException.class, () -> controller.declineCv(2L, feedback));
    }

    @Test
    void acceptsAndRefusesOffers() throws Throwable {
        JobOfferDto offer = JobOfferDto.builder().id(1L).build();
        FeedbackDto feedback = new FeedbackDto("feedback");
        when(service.acceptOffer(1L)).thenReturn(offer);
        when(service.refuseOffer(1L, feedback)).thenReturn(offer);
        assertEquals(offer, controller.acceptOffer(1L).getBody());
        assertEquals(offer, controller.refuseOffer(1L, feedback).getBody());

        when(service.acceptOffer(2L)).thenThrow(new JobOfferNotFoundException("missing"));
        assertThrows(JobOfferNotFoundException.class, () -> controller.acceptOffer(2L));
        when(service.refuseOffer(2L, feedback)).thenThrow(new InvalidStateTransitionException("invalid"));
        assertThrows(InvalidStateTransitionException.class, () -> controller.refuseOffer(2L, feedback));
    }

    @Test
    void getsAllCandidatures() {
        CandidatureDto.StudentInfoDto studentInfo = new CandidatureDto.StudentInfoDto("Jane", "Doe", "1234567", "jane@test.com");
        CandidatureDto.JobOfferInfoDto offerInfo = new CandidatureDto.JobOfferInfoDto("Developer", "Acme", 12);
        CandidatureDto dto = new CandidatureDto(1L, studentInfo, offerInfo, Status.PENDING, null, null);

        when(service.getAllCandidatures()).thenReturn(List.of(dto));
        assertEquals(List.of(dto), controller.getAllCandidatures().getBody());
        assertEquals(HttpStatus.OK, controller.getAllCandidatures().getStatusCode());

        when(service.getAllCandidatures()).thenReturn(List.of());
        assertEquals(List.of(), controller.getAllCandidatures().getBody());
    }

    @Test
    void createsContractWithRequestIds() {
        ContractCreationRequestDto request = new ContractCreationRequestDto(1L, 2L, 3L, 4L);
        ContractDto contract = ContractDto.builder().id(10L).build();
        when(service.createContract(1L, 2L, 3L, 4L)).thenReturn(contract);

        var response = controller.createContract(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(contract, response.getBody());
        verify(service).createContract(1L, 2L, 3L, 4L);
    }
}
