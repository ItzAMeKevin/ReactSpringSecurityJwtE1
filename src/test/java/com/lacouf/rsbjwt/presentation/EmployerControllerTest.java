package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserAppService userAppService;

    @MockitoBean
    private EmployerService employerService;

    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentRepository emprunteurRepository;

    @MockitoBean
    private EmployerRepository employerRepository;

    @MockitoBean
    private ManagerRepository managerRepository;

    @MockitoBean
    private UserAppRepository userAppRepository;


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
        userCreateDTO.setId(2L);
        userCreateDTO.setFirstName("Marie");
        userCreateDTO.setLastname("Tremblay");
        userCreateDTO.setEmail("marie@acme.com");
        userCreateDTO.setPassword("password123");
        userCreateDTO.setRole(Role.EMPLOYER);
        userCreateDTO.setCompanyName("ACME Inc.");
        userCreateDTO.setAdresseDTO(adresseDTO);
        userCreateDTO.setPhoneNumber("514-555-1234");
        userCreateDTO.setEmployerId("EMP001");


        jobOfferCreateDTO = new JobOfferCreateDTO(
                "Développeur Java",
                "Description du poste",
                "Spring Boot, JPA",
                adresseDTO,
                "60000$",
                LocalDate.of(2025, 3, 1),
                12,
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
                "ACME Inc."
        );

    }

    @Test
    @DisplayName("Post /employer/addJobOffer")
    void add_Job_Offer_succes() throws Exception {
        // ===== ARRANGE =====
        when(employerService.addJobOffer(any(JobOfferCreateDTO.class)))
                .thenReturn(jobOfferDetailDTO);

        // ===== ACT =====
        mockMvc.perform(post("/employer/addJobOffer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobOfferCreateDTO)))

                // ===== assert=====
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Développeur Java"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.employerId").value(2L));

        verify(employerService, times(1)).addJobOffer(any(JobOfferCreateDTO.class));

    }

    @Test
    void getJobOffre() {
    }
}