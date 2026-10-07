package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.EmployerService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    private StudentCvRepository studentCvRepository;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonMapper jsonMapper;

    private MockMvc mockMvc;
    private JobOfferDetailDTO testOfferDetailDto;
    private String requestBody;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        AdresseDTO adresse = AdresseDTO.builder()
                .pays("Canada").ville("Montreal").rue("Rue Principale")
                .numeroCivic("123").codePostal("H1A1A1")
                .build();

        testOfferDetailDto = new JobOfferDetailDTO(
                1L, "Dev Java", "Description", "Java, Spring",
                adresse, "20$/h", OfferStatus.WAITING,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 9, 1),
                16, Programe.TECHNIQUES_INFORMATIQUE, "Acme Corp"
        );

        JobOfferCreateDTO createDTO = new JobOfferCreateDTO(
                "Dev Java", "Description", "Java, Spring",
                adresse, "20$/h", LocalDate.of(2025, 9, 1),
                16, Programe.TECHNIQUES_INFORMATIQUE
        );

        try {
            requestBody = jsonMapper.writeValueAsString(createDTO);
        } catch (Exception e) {
            requestBody = "{}";
        }
    }

    @Test
    @DisplayName("POST /employer/addJobOffer returns 201 when employer submits offer")
    void addJobOffer_returnsCreated() throws Exception {
        when(employerService.addJobOffer(any(JobOfferCreateDTO.class), anyString()))
                .thenReturn(testOfferDetailDto);

        mockMvc.perform(post("/employer/addJobOffer")
                        .with(user("employer@test.com").authorities(new SimpleGrantedAuthority("EMPLOYER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Dev Java"))
                .andExpect(jsonPath("$.companyName").value("Acme Corp"));
    }

    @Test
    @DisplayName("POST /employer/addJobOffer returns 403 for non-employer")
    void addJobOffer_wrongRole_returns403() throws Exception {
        mockMvc.perform(post("/employer/addJobOffer")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}
