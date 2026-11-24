package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.exception.ApplicationStateException;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.model.InterviewType;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.JwtAuthenticationEntryPoint;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.security.SecurityConfiguration;
import com.lacouf.rsbjwt.service.StudentService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.InterviewDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import(SecurityConfiguration.class)
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
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private MockMvc mockMvc;
    private List<JobOfferDetailDTO> testOffers;
    private InterviewDto testInterview;

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

        testInterview = new InterviewDto(1L, 10L, "Acme Corp", "Dev Java",
                InterviewType.ONLINE, "https://zoom.us", null, null,
                Instant.now(), List.of(), Status.PENDING, null, false);
    }

    // ── GET /etudiant/offres ──────────────────────────────────────────────────

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
    @DisplayName("GET /etudiant/offres retourne 403 si mauvais rôle")
    void getAcceptedOffers_returns403_wrongRole() throws Exception {
        mockMvc.perform(get("/etudiant/offres")
                        .with(user("employer@test.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /etudiant/offres retourne 404 si l'étudiant n'existe pas")
    void getAcceptedOffers_returns404_whenStudentNotFound() throws Exception {
        when(studentService.getAcceptedJobOffers(anyString()))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/etudiant/offres")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isNotFound());
    }

    // ── GET /etudiant/entrevues ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /etudiant/entrevues retourne 200 avec la liste des entrevues")
    void getInterviews_returns200() throws Exception {
        when(studentService.getInterviews(anyString())).thenReturn(List.of(testInterview));

        mockMvc.perform(get("/etudiant/entrevues")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].offerTitle").value("Dev Java"))
                .andExpect(jsonPath("$[0].companyName").value("Acme Corp"));
    }

    @Test
    @DisplayName("GET /etudiant/entrevues retourne 403 si mauvais rôle")
    void getInterviews_returns403_wrongRole() throws Exception {
        mockMvc.perform(get("/etudiant/entrevues")
                        .with(user("employer@test.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                .andExpect(status().isForbidden());
    }

    // ── PUT /etudiant/entrevues/{id}/repondre ─────────────────────────────────

    @Test
    @DisplayName("PUT /etudiant/entrevues/{id}/repondre retourne 200 sur réponse valide")
    void respondToInterview_returns200() throws Exception {
        when(studentService.respondToInterview(eq(1L), any(), anyString())).thenReturn(testInterview);

        mockMvc.perform(put("/etudiant/entrevues/1/repondre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accepted\":true,\"message\":\"Je serai là\"}")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerTitle").value("Dev Java"));
    }

    @Test
    @DisplayName("PUT /etudiant/entrevues/{id}/repondre retourne 404 si entrevue non trouvée")
    void respondToInterview_returns404() throws Exception {
        when(studentService.respondToInterview(eq(99L), any(), anyString()))
                .thenThrow(new ApplicationNotFoundException());

        mockMvc.perform(put("/etudiant/entrevues/99/repondre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accepted\":true,\"message\":\"ok\"}")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /etudiant/entrevues/{id}/repondre retourne 409 si déjà répondu")
    void respondToInterview_returns409_alreadyResponded() throws Exception {
        when(studentService.respondToInterview(eq(1L), any(), anyString()))
                .thenThrow(new ApplicationStateException());

        mockMvc.perform(put("/etudiant/entrevues/1/repondre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accepted\":false,\"message\":\"Pas disponible\"}")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /etudiant/entrevues/{id}/repondre retourne 403 si mauvais rôle")
    void respondToInterview_returns403_wrongRole() throws Exception {
        mockMvc.perform(put("/etudiant/entrevues/1/repondre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accepted\":true,\"message\":null}")
                        .with(user("employer@test.com").authorities(new SimpleGrantedAuthority("EMPLOYER"))))
                .andExpect(status().isForbidden());
    }
}
