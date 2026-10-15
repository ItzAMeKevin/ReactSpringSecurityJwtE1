package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class StudentControllerWebMvcTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private StudentService studentService;

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

    private MockMvc mockMvc;
    private List<JobOfferDetailDTO> testOffers;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        AdresseDTO adresse = AdresseDTO.builder()
                .pays("Canada").ville("Montreal").rue("Rue Principale")
                .numeroCivic("123").codePostal("H1A1A1")
                .build();

        testOffers = List.of(new JobOfferDetailDTO(
                1L, "Dev Java", "Description", "Java",
                adresse, "20$/h", Status.ACCEPTED,
                LocalDate.now(), LocalDate.of(2025, 9, 1),
                16, Programe.TECHNIQUES_INFORMATIQUE, "Acme Corp"
        ));
    }

    @Test
    @DisplayName("GET /etudiant/offres retourne 200 avec la liste des offres")
    void getAcceptedOffers_returns200() throws Exception {
        when(studentService.getAcceptedJobOffers(anyString())).thenReturn(testOffers);

        mockMvc.perform(get("/etudiant/offres")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dev Java"))
                .andExpect(jsonPath("$[0].companyName").value("Acme Corp"));
    }

    @Test
    @DisplayName("GET /etudiant/offres retourne 403 si pas de CV validé")
    void getAcceptedOffers_returns403_whenNoCvValidated() throws Exception {
        when(studentService.getAcceptedJobOffers(anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No validated CV"));

        mockMvc.perform(get("/etudiant/offres")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /etudiant/offres retourne 403 si mauvais rôle")
    void getAcceptedOffers_returns403_wrongRole() throws Exception {
        mockMvc.perform(get("/etudiant/offres")
                        .with(user("employer@test.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /etudiant/offres retourne 401 si non authentifié")
    void getAcceptedOffers_returns401_unauthenticated() throws Exception {
        mockMvc.perform(get("/etudiant/offres"))
                .andExpect(status().isUnauthorized());
    }


}
