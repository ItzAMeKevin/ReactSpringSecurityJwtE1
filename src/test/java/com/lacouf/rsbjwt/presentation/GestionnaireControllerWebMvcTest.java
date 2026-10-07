package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class GestionnaireControllerWebMvcTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

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
    private EmployerNotificationRepository employerNotificationRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private JobOfferDetailDTO testOfferDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        AdresseDTO adresse = new AdresseDTO("Canada", "Montreal", "Rue Principale", "123", "H1A1A1");
        testOfferDto = new JobOfferDetailDTO(
                1L,
                "Dev Java",
                "Développement d'applications Java",
                "Connaissance de Java et Spring Boot",
                adresse,
                "50000",
                null,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                12,
                null,
                "Acme Corp"
        );
    }

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
}