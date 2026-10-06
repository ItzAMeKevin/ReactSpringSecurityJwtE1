package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployerController.class)
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

    @Autowired
    private MockMvc mockMvc;

    private AdresseDTO adresseDTO;
    private UserCreateDTO userCreateDTO;
    private JobOfferCreateDTO jobOfferCreateDTO;
    private JobOfferDetailDTO jobOfferDetailDTO;

    @BeforeEach
    void setUp() {
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
                Programe.TECHNIQUES_INFORMATIQUE,
                2L
        );

        jobOfferDetailDTO = new JobOfferDetailDTO(
                1L,
                "Développeur Java",
                "Description du poste",
                "Spring Boot, JPA",
                adresseDTO,
                "60000$",
                OfferStatus.WAITING,
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
        when(employerService.addJobOffer(any(JobOfferCreateDTO.class)))
                .thenReturn(jobOfferDetailDTO);

        // ===== ACT =====
        mockMvc.perform(post("/employer/addJobOffer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(jobOfferCreateDTO)))

                // ===== assert=====
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Développeur Java"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.companyName").value("ACME Inc."));

        verify(employerService, times(1)).addJobOffer(any(JobOfferCreateDTO.class));

    }

    @Test
    void addJobOffer_devraitRetourner400SiBodyInvalide() throws Exception {
        // ===== ARRANGE =====


        // ===== ACT =====
        mockMvc.perform(post("/employer/addJobOffer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                // ===== assert=====
                .andExpect(status().isBadRequest());   // 400


        verify(employerService, never()).addJobOffer(any());
    }

    @Test
    void getJobOffer_devraitRetournerLaListeDuService() throws Exception {
        // ───── ARRANGE ─────
        Long employerId = 42L;
        List<JobOfferDetailDTO> expected = List.of(

                new JobOfferDetailDTO(
                        1L,
                        "Développeur Java",
                        "Description du poste",
                        "Spring Boot, JPA",
                        adresseDTO,
                        "60000$",
                        OfferStatus.WAITING,
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
                        OfferStatus.ACCEPTED,
                        LocalDate.now(),
                        LocalDate.of(2025, 4, 1),
                        6,
                        Programe.TECHNIQUES_INFORMATIQUE,
                        "ACME Inc."
                )
        );
        when(employerService.getJobOffres(employerId)).thenReturn(expected);


        // ===== ACT =====
        mockMvc.perform(get("/employer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(employerId)))
                // ===== assert=====
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(employerService).getJobOffres(employerId);
    }
}