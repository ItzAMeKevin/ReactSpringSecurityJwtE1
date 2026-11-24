package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.JwtAuthenticationEntryPoint;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.security.SecurityConfiguration;
import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import({SecurityConfiguration.class, JwtAuthenticationEntryPoint.class})
@ActiveProfiles("test")
class StudentControllerWebMvcTest {

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private UserAppService userAppService;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext webApplicationContext;

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

    private static RequestPostProcessor student() {
        return user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"));
    }

    @Test
    @DisplayName("GET /etudiant/offres retourne 200 avec la liste des offres")
    void getAcceptedOffers_returns200() throws Exception {
        when(studentService.getAcceptedJobOffers(anyString())).thenReturn(testOffers);

        mockMvc.perform(get("/etudiant/offres").with(student()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dev Java"))
                .andExpect(jsonPath("$[0].companyName").value("Acme Corp"));
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

    @Test
    @DisplayName("GET /etudiant/offres retourne 404 si l'étudiant n'existe pas")
    void getAcceptedOffers_returns404_whenStudentNotFound() throws Exception {
        when(studentService.getAcceptedJobOffers(anyString())).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/etudiant/offres").with(student()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /etudiant/embauches retourne les offres d'embauche en attente de réponse")
    void getHires_returns200() throws Exception {
        CandidatureDto dto = new CandidatureDto(7L, null, null, Status.HIRE_PENDING, null, null);
        when(studentService.getHires("student@test.com")).thenReturn(List.of(dto));

        mockMvc.perform(get("/etudiant/embauches").with(student()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].status").value("HIRE_PENDING"));
    }

    @Test
    @DisplayName("PUT /etudiant/embauches/{id}/accepter accepte l'offre d'embauche")
    void acceptHire_returns200() throws Exception {
        CandidatureDto dto = new CandidatureDto(7L, null, null, Status.AWAITING_CONTRACT, null, null);
        when(studentService.acceptHire(7L, "student@test.com")).thenReturn(dto);

        mockMvc.perform(put("/etudiant/embauches/7/accepter").with(student()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AWAITING_CONTRACT"));
    }

    @Test
    @DisplayName("PUT /etudiant/embauches/{id}/decliner refuse l'offre d'embauche")
    void declineHire_returns200() throws Exception {
        CandidatureDto dto = new CandidatureDto(7L, null, null, Status.DECLINED, null, null);
        when(studentService.declineHire(7L, "student@test.com")).thenReturn(dto);

        mockMvc.perform(put("/etudiant/embauches/7/decliner").with(student()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));
    }

    @Test
    @DisplayName("PUT /etudiant/embauches/{id}/accepter retourne 403 pour un employeur")
    void hireEndpoints_returns403_forEmployer() throws Exception {
        mockMvc.perform(put("/etudiant/embauches/7/accepter")
                        .with(user("employer@test.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                .andExpect(status().isForbidden());
        verifyNoInteractions(studentService);
    }
}
