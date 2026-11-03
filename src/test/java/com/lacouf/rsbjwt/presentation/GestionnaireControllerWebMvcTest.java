package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.SystemeService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.CvDeclineFeedbackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class GestionnaireControllerWebMvcTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private JobOfferDto testOfferDto;

    @MockitoBean
    private GestionnaireService gestionnaireService;

    @MockitoBean
    private UserAppService userAppService;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private EmployerRepository employerRepository;

    @MockitoBean
    private ManagerRepository managerRepository;

    @MockitoBean
    private JobOfferRepository jobOfferRepository;


    @MockitoBean
    private StudentCvRepository studentCvRepository;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private SystemeService systemeService;

    private Authentication managerAuthentication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        managerAuthentication = new UsernamePasswordAuthenticationToken(
                "manager@test.com",
                null,
                List.of(new SimpleGrantedAuthority("MANAGER"))
        );

        AdresseDTO adresse = new AdresseDTO("Canada", "Montreal", "Rue Principale", "123", "H1A1A1");
        testOfferDto = new JobOfferDto(
                1L, "Dev Java", "Développement backend", "Java, Spring",
                adresse, "20$/h", LocalDate.of(2025, 9, 1),
                16, "Acme Corp", LocalDate.of(2025, 1, 1)
        );
    }

    // ── CV Management ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /gestionnaire/cv/pending returns 200 and list of pending CVs")
    void getPendingCvs_success_returnsOkAndList() throws Exception {
        PendingCvDto dto = PendingCvDto.builder()
                .id(1L)
                .fileName("cv.pdf")
                .studentFirstName("John")
                .studentLastName("Doe")
                .studentMatricule("1234567")
                .build();
        when(gestionnaireService.getPendingCvs()).thenReturn(List.of(dto));

        mockMvc.perform(get("/gestionnaire/cv/pending")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fileName").value("cv.pdf"))
                .andExpect(jsonPath("$[0].studentFirstName").value("John"))
                .andExpect(jsonPath("$[0].studentLastName").value("Doe"));
    }

    @Test
    @DisplayName("GET /gestionnaire/cv/{cvId}/content returns 200 and PDF bytes")
    void getCvContent_success_returnsOkAndBytes() throws Exception {
        byte[] pdfBytes = new byte[]{37, 80, 68, 70, 45};
        when(gestionnaireService.getCvContent(1L)).thenReturn(pdfBytes);

        mockMvc.perform(get("/gestionnaire/cv/1/content")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    @DisplayName("GET /gestionnaire/cv/{cvId}/content returns 404 when CV not found")
    void getCvContent_notFound_returns404() throws Exception {
        when(gestionnaireService.getCvContent(99L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/gestionnaire/cv/99/content")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /gestionnaire/cv/{cvId}/accept returns 204 on success")
    void acceptCv_success_returnsNoContent() throws Exception {
        doNothing().when(gestionnaireService).acceptCv(1L);

        mockMvc.perform(put("/gestionnaire/cv/1/accept")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER"))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /gestionnaire/cv/{cvId}/accept returns 404 when CV not found")
    void acceptCv_notFound_returns404() throws Exception {
        doThrow(new UserNotFoundException()).when(gestionnaireService).acceptCv(99L);

        mockMvc.perform(put("/gestionnaire/cv/99/accept")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /gestionnaire/cv/{cvId}/decline returns 204 on success")
    void declineCv_success_returnsNoContent() throws Exception {
        CvDeclineFeedbackDto feedbackRequest = new CvDeclineFeedbackDto("Le CV ne respecte pas le format.");
        doNothing().when(gestionnaireService).declineCv(eq(1L), any(CvDeclineFeedbackDto.class));

        mockMvc.perform(put("/gestionnaire/cv/1/decline")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /gestionnaire/cv/{cvId}/decline returns 404 when CV not found")
    void declineCv_notFound_returns404() throws Exception {
        CvDeclineFeedbackDto feedbackRequest = new CvDeclineFeedbackDto("Feedback");
        doThrow(new UserNotFoundException()).when(gestionnaireService).declineCv(eq(99L), any(CvDeclineFeedbackDto.class));

        mockMvc.perform(put("/gestionnaire/cv/99/decline")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackRequest)))
                .andExpect(status().isNotFound());
    }

    // ── Job Offer Management ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /gestionnaire/offres/pending returns 200 with list")
    @WithMockUser(authorities = "MANAGER")
    void getPendingOffers_returnsOk() throws Exception {
        when(gestionnaireService.getPendingOffers()).thenReturn(List.of(testOfferDto));

        mockMvc.perform(get("/gestionnaire/offres/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Dev Java"))
                .andExpect(jsonPath("$[0].companyName").value("Acme Corp"));
    }

    @Test
    @DisplayName("GET /gestionnaire/offres/pending returns 200 with empty list")
    @WithMockUser(authorities = "MANAGER")
    void getPendingOffers_empty_returnsOk() throws Exception {
        when(gestionnaireService.getPendingOffers()).thenReturn(List.of());

        mockMvc.perform(get("/gestionnaire/offres/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("PUT /gestionnaire/offres/{id}/accept returns 200 when found")
    @WithMockUser(authorities = "MANAGER")
    void acceptOffer_found_returnsOk() throws Exception {
        when(gestionnaireService.acceptOffer(1L)).thenReturn(Optional.of(testOfferDto));

        mockMvc.perform(put("/gestionnaire/offres/1/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dev Java"));
    }

    @Test
    @DisplayName("PUT /gestionnaire/offres/{id}/accept returns 404 when not found")
    @WithMockUser(authorities = "MANAGER")
    void acceptOffer_notFound_returns404() throws Exception {
        when(gestionnaireService.acceptOffer(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/gestionnaire/offres/99/accept"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /gestionnaire/offres/{id}/refuse returns 200 when found")
    @WithMockUser(authorities = "MANAGER")
    void refuseOffer_found_returnsOk() throws Exception {
        when(gestionnaireService.refuseOffer(1L)).thenReturn(Optional.of(testOfferDto));

        mockMvc.perform(put("/gestionnaire/offres/1/refuse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dev Java"));
    }

    @Test
    @DisplayName("PUT /gestionnaire/offres/{id}/refuse returns 404 when not found")
    @WithMockUser(authorities = "MANAGER")
    void refuseOffer_notFound_returns404() throws Exception {
        when(gestionnaireService.refuseOffer(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/gestionnaire/offres/99/refuse"))
                .andExpect(status().isNotFound());
    }

    // ── Manager Notifications (job-offer events) ──────────────────────────────

    @Test
    @DisplayName("GET /gestionnaire/offres/notifications returns 200 with list")
    void getNotifications_returnsOk() throws Exception {
        NotificationDto notif = NotificationDto.builder()
                .id(1L)
                .notificationType(NotificationType.JOB_OFFER)
                .referenceId(1L)
                .status(Status.UNREAD)
                .creationDate(Instant.now())
                .build();
        when(systemeService.getNotifications(managerAuthentication)).thenReturn(List.of(notif));

        mockMvc.perform(get("/gestionnaire/offres/notifications")
                        .with(authentication(managerAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationType").value("JOB_OFFER"))
                .andExpect(jsonPath("$[0].referenceId").value(1));
    }

    @Test
    @DisplayName("GET /gestionnaire/offres/notifications returns 200 with empty list")
    void getNotifications_empty_returnsOk() throws Exception {
        when(systemeService.getNotifications(managerAuthentication)).thenReturn(List.of());

        mockMvc.perform(get("/gestionnaire/offres/notifications")
                        .with(authentication(managerAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("PUT /gestionnaire/offres/notifications/{id}/read returns 200 when found")
    void markAsRead_found_returnsOk() throws Exception {
        NotificationDto notif = NotificationDto.builder()
                .id(1L)
                .notificationType(NotificationType.JOB_OFFER)
                .referenceId(1L)
                .status(Status.READ)
                .creationDate(Instant.now())
                .build();
        when(systemeService.updateNotificationStatus(managerAuthentication, 1L)).thenReturn(notif);

        mockMvc.perform(put("/gestionnaire/offres/notifications/1/read")
                        .with(authentication(managerAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READ"));
    }

    @Test
    @DisplayName("PUT /gestionnaire/offres/notifications/{id}/read returns 404 when not found")
    void markAsRead_notFound_returns404() throws Exception {
        when(systemeService.updateNotificationStatus(managerAuthentication, 99L))
                .thenThrow(new NotificationNotFound());

        mockMvc.perform(put("/gestionnaire/offres/notifications/99/read")
                        .with(authentication(managerAuthentication)))
                .andExpect(status().isNotFound());
    }
}
