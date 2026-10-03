package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class EmployerControllerWebMvcTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private EmployerService employerService;

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
    private ManagerNotificationRepository managerNotificationRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private JobOfferDto testOfferDto;
    private String requestBody;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        requestBody = """
                {
                    "title": "Dev Java",
                    "description": "Description",
                    "prerequisites": "Java, Spring",
                    "adresse": {
                        "pay": "Canada",
                        "ville": "Montreal",
                        "rue": "Rue Principale",
                        "numeroCivic": "123",
                        "codePostal": "H1A1A1"
                    },
                    "salary": "20$/h",
                    "startingDate": "2025-09-01",
                    "durationInWeeks": 16,
                    "programe": "TECHNIQUES_INFORMATIQUE"
                }
                """;

        AdresseDTO adresse = new AdresseDTO("Canada", "Montreal", "Rue Principale", "123", "H1A1A1");
        testOfferDto = new JobOfferDto(
                1L, "Dev Java", "Description", "Java, Spring",
                adresse, "20$/h", LocalDate.of(2025, 9, 1),
                16, "Acme Corp", LocalDate.of(2025, 1, 1)
        );
    }

    @Test
    @DisplayName("POST /employer/offres returns 200 when employer submits offer")
    @WithMockUser(authorities = "EMPLOYER")
    void submitOffer_returnsOk() throws Exception {
        when(employerService.submitOffer(anyString(), any(JobOfferCreateDto.class)))
                .thenReturn(Optional.of(testOfferDto));

        mockMvc.perform(post("/employer/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dev Java"))
                .andExpect(jsonPath("$.companyName").value("Acme Corp"));
    }

    @Test
    @DisplayName("POST /employer/offres returns 404 when employer not found")
    @WithMockUser(authorities = "EMPLOYER")
    void submitOffer_employerNotFound_returns404() throws Exception {
        when(employerService.submitOffer(anyString(), any(JobOfferCreateDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/employer/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /employer/offres returns 403 for non-employer")
    @WithMockUser(authorities = "STUDENT")
    void submitOffer_wrongRole_returns403() throws Exception {
        mockMvc.perform(post("/employer/offres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}
