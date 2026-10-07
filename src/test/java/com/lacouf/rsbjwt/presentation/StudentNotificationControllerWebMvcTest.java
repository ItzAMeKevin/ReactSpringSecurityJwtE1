package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class StudentNotificationControllerWebMvcTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private GestionnaireMapper gestionnaireMapper;

    @MockitoBean
    private GestionnaireService gestionnaireService;

    @MockitoBean
    private UserAppService userService;

    @MockitoBean
    private EmployerRepository employerRepository;

    @MockitoBean
    private ManagerRepository managerRepository;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @MockitoBean
    private StudentCvRepository studentCvRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private Student testStudent;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        testStudent = new Student();
        testStudent.setId(1L);

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setTitle("Votre CV a été accepté");
        testNotification.setMessage("Votre CV a été validé.");
        testNotification.setStudent(testStudent);
    }

    @Test
    @DisplayName("GET /etudiant/notifications returns 200 and list of notifications")
    void getNotifications_success_returnsOkAndList() throws Exception {
        NotificationDto dto = NotificationDto.builder()
                .id(1L)
                .title("Votre CV a été accepté")
                .message("Votre CV a été validé.")
                .isRead(false)
                .build();

        when(studentRepository.findByCredentialsEmail("student@test.com"))
                .thenReturn(Optional.of(testStudent));
        when(notificationRepository.findAllByStudent_IdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(testNotification));
        when(gestionnaireMapper.toNotificationDto(testNotification))
                .thenReturn(dto);

        mockMvc.perform(get("/etudiant/notifications")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Votre CV a été accepté"))
                .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    @DisplayName("PUT /etudiant/notifications/{id}/read returns 204")
    void markAsRead_success_returnsNoContent() throws Exception {
        when(studentRepository.findByCredentialsEmail("student@test.com"))
                .thenReturn(Optional.of(testStudent));
        when(notificationRepository.findByIdAndStudent_Id(1L, 1L))
                .thenReturn(Optional.of(testNotification));

        mockMvc.perform(put("/etudiant/notifications/1/read")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT"))))
                .andExpect(status().isNoContent());
    }
}