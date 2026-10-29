package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.JwtAuthenticationEntryPoint;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.security.SecurityConfiguration;
import com.lacouf.rsbjwt.security.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import com.lacouf.rsbjwt.service.dto.UpdateCandidateStatusDto;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(EmployerController.class)
@Import(SecurityConfiguration.class)
@ActiveProfiles("test")
class EmployerControllerTest {

    @MockitoBean
    private UserAppService userAppService;

    @MockitoBean
    private EmployerService employerService;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private AdresseDTO adresseDTO;
    private UserCreateDTO userCreateDTO;
    private JobOfferCreateDTO jobOfferCreateDTO;
    private JobOfferDetailDTO jobOfferDetailDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        adresseDTO = AdresseDTO.builder()
                .pays("Canada")
                .ville("Montréal")
                .rue("Sainte-Catherine")
                .numeroCivic("100")
                .codePostal("H2X 1Y5")
                .build();

        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName("Marie");
        userCreateDTO.setLastname("Tremblay");
        userCreateDTO.setEmail("marie@acme.com");
        userCreateDTO.setPassword("password123");
        userCreateDTO.setRole(Role.EMPLOYER);
        userCreateDTO.setCompanyName("ACME Inc.");
        userCreateDTO.setAdresseDTO(adresseDTO);
        userCreateDTO.setPhoneNumber("514-555-1234");
        userCreateDTO.setEmployerWorkId("EMP001");


        jobOfferCreateDTO = new JobOfferCreateDTO(
                "Développeur Java",
                "Description du poste",
                "Spring Boot, JPA",
                adresseDTO,
                "60000$",
                LocalDate.of(2025, 3, 1),
                12,
                Programe.TECHNIQUES_INFORMATIQUE
        );

        jobOfferDetailDTO = new JobOfferDetailDTO(
                1L,
                "Développeur Java",
                "Description du poste",
                "Spring Boot, JPA",
                adresseDTO,
                "60000$",
                Status.WAITING,
                LocalDate.now(),
                LocalDate.of(2025, 3, 1),
                12,
                Programe.TECHNIQUES_INFORMATIQUE,
                "ACME Inc."
        );

    }

    @Test
    @DisplayName("Post /employer/addJobOffer")
    void add_Job_Offer_succes_devrai_retour_201() throws Exception {
        // ===== ARRANGE =====
        when(employerService.addJobOffer(any(JobOfferCreateDTO.class), anyString()))
                .thenReturn(jobOfferDetailDTO);

        // ===== ACT =====
        mockMvc.perform(post("/employer/addJobOffer")
                        .with(user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(jobOfferCreateDTO)))

                // ===== assert=====
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Développeur Java"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.companyName").value("ACME Inc."));

        verify(employerService, times(1)).addJobOffer(any(JobOfferCreateDTO.class), anyString());

    }

    @Test
    void addJobOffer_devraitRetourner400SiBodyInvalide() throws Exception {
        // ===== ARRANGE =====


        // ===== ACT =====
        mockMvc.perform(post("/employer/addJobOffer")
                        .with(user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                // ===== assert=====
                .andExpect(status().isBadRequest());   // 400


        verify(employerService, never()).addJobOffer(any(), anyString());
    }

    @Test
    void getJobOffer_devraitRetournerLaListeDuService() throws Exception {
        // ───── ARRANGE ─────
        List<JobOfferDetailDTO> expected = List.of(

                new JobOfferDetailDTO(
                        1L,
                        "Développeur Java",
                        "Description du poste",
                        "Spring Boot, JPA",
                        adresseDTO,
                        "60000$",
                        Status.WAITING,
                        LocalDate.now(),
                        LocalDate.of(2025, 3, 1),
                        12,
                        Programe.TECHNIQUES_INFORMATIQUE,
                        "ACME Inc."
                ),
                new JobOfferDetailDTO(
                        2L,
                        "Designer UX",
                        "Description du poste 2",
                        "Figma, Sketch",
                        adresseDTO,
                        "50000$",
                        Status.ACCEPTED,
                        LocalDate.now(),
                        LocalDate.of(2025, 4, 1),
                        6,
                        Programe.TECHNIQUES_INFORMATIQUE,
                        "ACME Inc."
                )
        );
        when(employerService.getJobOffres(anyString())).thenReturn(expected);


        // ===== ACT =====
        mockMvc.perform(get("/employer")
                        .with(user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                // ===== assert=====
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(employerService).getJobOffres(anyString());
    }

    @Test
    @DisplayName("GET /employer/{id}/candidates retourne la liste")
    void getCandidates_returnsList() throws Exception {
        CandidateDto candidate = new CandidateDto(
                5L, "Jean", "Dupont", "jean@test.com",
                Instant.parse("2026-02-01T12:00:00Z"), Status.PENDING);
        when(employerService.getCandidates(1L, "marie@acme.com")).thenReturn(List.of(candidate));

        mockMvc.perform(get("/employer/1/candidates")
                        .with(user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].cvId").value(5))
                .andExpect(jsonPath("$[0].firstName").value("Jean"))
                .andExpect(jsonPath("$[0].lastName").value("Dupont"))
                .andExpect(jsonPath("$[0].email").value("jean@test.com"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /employer/{id}/candidates liste vide retourne 200")
    void getCandidates_emptyList() throws Exception {
        when(employerService.getCandidates(1L, "marie@acme.com")).thenReturn(List.of());

        mockMvc.perform(get("/employer/1/candidates")
                        .with(user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /employer/{id}/candidates refuse un étudiant")
    void getCandidates_studentForbidden() throws Exception {
        mockMvc.perform(get("/employer/1/candidates")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /employer/{id}/candidates/{cvId}/status met à jour le statut")
    void updateCandidateStatus_ok() throws Exception {
        CandidateDto updated = new CandidateDto(
                5L, "Jean", "Dupont", "jean@test.com",
                Instant.parse("2026-02-01T12:00:00Z"), Status.ACCEPTED);
        when(employerService.updateCandidateStatus(1L, 5L, Status.ACCEPTED, "marie@acme.com"))
                .thenReturn(updated);

        mockMvc.perform(put("/employer/1/candidates/5/status")
                        .with(user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new UpdateCandidateStatusDto(Status.ACCEPTED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("GET /employer/candidates/{cvId}/cv retourne un PDF")
    void getCandidateCv_returnsPdf() throws Exception {
        when(employerService.getCandidateCv(5L, "marie@acme.com")).thenReturn(new byte[]{37, 80, 68, 70});

        mockMvc.perform(get("/employer/candidates/5/cv")
                        .with(user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
    private static RequestPostProcessor employer() {
        return user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER"));
    }

    private String statusBody() throws Exception {
        return jsonMapper.writeValueAsString(new UpdateCandidateStatusDto(Status.ACCEPTED));
    }

    @Test
    void getCandidates_notFound_returns404() throws Exception {
        when(employerService.getCandidates(1L, "marie@acme.com")).thenThrow(new ApplicationNotFoundException());
        mockMvc.perform(get("/employer/1/candidates").with(employer())).andExpect(status().isNotFound());
    }

    @Test
    void getCandidates_error_returns500() throws Exception {
        when(employerService.getCandidates(1L, "marie@acme.com")).thenThrow(new RuntimeException("boom"));
        mockMvc.perform(get("/employer/1/candidates").with(employer())).andExpect(status().isInternalServerError());
    }

    @Test
    void updateCandidateStatus_notFound_returns404() throws Exception {
        when(employerService.updateCandidateStatus(1L, 5L, Status.ACCEPTED, "marie@acme.com")).thenThrow(new ApplicationNotFoundException());
        mockMvc.perform(put("/employer/1/candidates/5/status").with(employer())
                        .contentType(MediaType.APPLICATION_JSON).content(statusBody()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCandidateStatus_illegalArgument_returns400() throws Exception {
        when(employerService.updateCandidateStatus(1L, 5L, Status.ACCEPTED, "marie@acme.com")).thenThrow(new IllegalArgumentException());
        mockMvc.perform(put("/employer/1/candidates/5/status").with(employer())
                        .contentType(MediaType.APPLICATION_JSON).content(statusBody()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCandidateStatus_illegalState_returns409() throws Exception {
        when(employerService.updateCandidateStatus(1L, 5L, Status.ACCEPTED, "marie@acme.com")).thenThrow(new IllegalStateException());
        mockMvc.perform(put("/employer/1/candidates/5/status").with(employer())
                        .contentType(MediaType.APPLICATION_JSON).content(statusBody()))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCandidateStatus_error_returns500() throws Exception {
        when(employerService.updateCandidateStatus(1L, 5L, Status.ACCEPTED, "marie@acme.com")).thenThrow(new RuntimeException("boom"));
        mockMvc.perform(put("/employer/1/candidates/5/status").with(employer())
                        .contentType(MediaType.APPLICATION_JSON).content(statusBody()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCandidateCv_notOwned_returns404() throws Exception {
        when(employerService.getCandidateCv(5L, "marie@acme.com")).thenThrow(new ApplicationNotFoundException());
        mockMvc.perform(get("/employer/candidates/5/cv").with(employer())).andExpect(status().isNotFound());
    }

    @Test
    void getCandidateCv_invalidCv_returns404() throws Exception {
        when(employerService.getCandidateCv(5L, "marie@acme.com")).thenThrow(new InvalidCvException("CV not found"));
        mockMvc.perform(get("/employer/candidates/5/cv").with(employer())).andExpect(status().isNotFound());
    }

    @Test
    void getCandidateCv_error_returns500() throws Exception {
        when(employerService.getCandidateCv(5L, "marie@acme.com")).thenThrow(new RuntimeException("boom"));
        mockMvc.perform(get("/employer/candidates/5/cv").with(employer())).andExpect(status().isInternalServerError());
    }
}