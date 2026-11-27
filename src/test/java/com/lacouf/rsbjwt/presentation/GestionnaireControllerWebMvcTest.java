package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.*;

import com.lacouf.rsbjwt.model.Programe;
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
        StudentDetailsDto student = new StudentDetailsDto(2L, "Jane", "Doe", "1234567", "jane@test.com", Programe.TECHNIQUES_INFORMATIQUE);
        JobOfferDetailDTO offer = JobOfferDetailDTO.builder().id(3L).title("Developer").companyName("Acme").durationInWeeks(12).build();
        JobApplicationSuiviDto dto = new JobApplicationSuiviDto(1L, Status.PENDING, null, null, student, offer, null);

        when(service.getAllCandidatures()).thenReturn(List.of(dto));
        assertEquals(List.of(dto), controller.getAllCandidatures().getBody());
        assertEquals(HttpStatus.OK, controller.getAllCandidatures().getStatusCode());

        when(service.getAllCandidatures()).thenReturn(List.of());
        assertEquals(List.of(), controller.getAllCandidatures().getBody());
    }

    @Test
    void createsContractWithCandidatureDtoAndManagerId() {
        CandidatureDto candidatureDto = new CandidatureDto(5L, null, null, null, null, null);
        ContractDto contract = ContractDto.builder().id(10L).build();
        when(service.createContract(candidatureDto, 3L)).thenReturn(contract);

        var response = controller.createContract(candidatureDto, 3L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(contract, response.getBody());
        verify(service).createContract(candidatureDto, 3L);
    }

    @Test
    void getPendingInternshipSitesReturnsOkWithList() {
        PendingInternshipSiteDto site = new PendingInternshipSiteDto(1L, "Alice", "Smith", "TechCorp", "2025-01-15");
        when(service.getPendingInternshipSites()).thenReturn(List.of(site));

        var response = controller.getPendingInternshipSites();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(site), response.getBody());
    }

    @Test
    void submitEvaluationReturnsOk() {
        InternshipSiteEvaluationDto dto = emptyDto();
        doNothing().when(service).submitInternshipSiteEvaluation(1L, dto);

        assertEquals(HttpStatus.OK, controller.submitInternshipSiteEvaluation(1L, dto).getStatusCode());
    }

    @Test
    void submitEvaluationPropagatesServiceExceptions() {
        InternshipSiteEvaluationDto dto = emptyDto();
        doThrow(new ApplicationNotFoundException()).when(service).submitInternshipSiteEvaluation(1L, dto);
        assertThrows(ApplicationNotFoundException.class, () -> controller.submitInternshipSiteEvaluation(1L, dto));

        doThrow(new OfferAlreadyProcessedException("errors.evaluationAlreadySubmitted"))
                .when(service).submitInternshipSiteEvaluation(2L, dto);
        assertThrows(OfferAlreadyProcessedException.class, () -> controller.submitInternshipSiteEvaluation(2L, dto));
    }

    private static InternshipSiteEvaluationDto emptyDto() {
        return new InternshipSiteEvaluationDto(
                null, null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null
        );
    }
}
