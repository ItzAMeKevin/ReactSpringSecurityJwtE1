package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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

    @MockitoBean
    private GestionnaireService gestionnaireService;

    @MockitoBean
    private UserAppService userService;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private EmployerRepository employerRepository;

    @MockitoBean
    private ManagerRepository managerRepository;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @MockitoBean
    private StudentCvRepository studentCvRepository;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
    }

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
        CvUploadDto reviewRequest = new CvUploadDto("review.pdf", "application/pdf",
                Base64.getEncoder().encodeToString(new byte[]{1, 2, 3}));
        doNothing().when(gestionnaireService).declineCv(eq(1L), any(CvUploadDto.class));

        mockMvc.perform(put("/gestionnaire/cv/1/decline")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /gestionnaire/cv/{cvId}/decline returns 404 when CV not found")
    void declineCv_notFound_returns404() throws Exception {
        CvUploadDto reviewRequest = new CvUploadDto("review.pdf", "application/pdf",
                Base64.getEncoder().encodeToString(new byte[]{1, 2, 3}));
        doThrow(new UserNotFoundException()).when(gestionnaireService).declineCv(eq(99L), any(CvUploadDto.class));

        mockMvc.perform(put("/gestionnaire/cv/99/decline")
                        .with(user("manager").authorities(new SimpleGrantedAuthority("MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isNotFound());
    }
}
