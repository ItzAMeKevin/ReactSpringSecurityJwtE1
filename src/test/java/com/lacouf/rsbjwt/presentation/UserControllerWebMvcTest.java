package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.LoginDTO;
import com.lacouf.rsbjwt.service.dto.UserDTO;
import com.lacouf.rsbjwt.model.auth.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerWebMvcTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAppService userService;

    @MockitoBean
    private StudentRepository emprunteurRepository;

    @MockitoBean
    private EmployerRepository preposeRepository;

    @MockitoBean
    private ManagerRepository managerRepository;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /user/login returns 202 and token on success")
    void authenticateUser_success_returnsAcceptedAndToken() throws Exception {
        // Arrange
        LoginDTO login = new LoginDTO("user@example.com", "password");
        when(userService.authenticateUser(any(LoginDTO.class))).thenReturn("token123");

        // Act + Assert
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tokenType").value("BEARER"))
                .andExpect(jsonPath("$.accessToken").value("token123"));
    }

    @Test
    @DisplayName("POST /user/login returns 401 on failure")
    void authenticateUser_failure_returnsUnauthorized() throws Exception {
        // Arrange
        LoginDTO login = new LoginDTO("user@example.com", "wrong");
        when(userService.authenticateUser(any(LoginDTO.class))).thenThrow(new RuntimeException("bad creds"));

        // Act + Assert
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tokenType").value("BEARER"))
                .andExpect(jsonPath("$.accessToken").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    @DisplayName("POST /user/inscription returns 202 and the created user")
    void inscription_success_returnsAcceptedAndUser() throws Exception {
        // Arrange
        UserDTO request = new UserDTO(null, "Jane", "Doe", "jane@example.com", "password", Role.STUDENT, "MAT123");
        UserDTO existingUser = new UserDTO(null, "John", "Doe", "john@example.com", null, Role.STUDENT, "MAT456");
        UserDTO createdUser = new UserDTO(1L, "Jane", "Doe", "jane@example.com", null, Role.STUDENT, "MAT123");

        // Act
        when(userService.getUserByEmail(request.getEmail())).thenReturn(existingUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userService.inscription(any(UserDTO.class))).thenReturn(createdUser);
        
        mockMvc.perform(post("/user/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.matricule").value("MAT123"));


        // Assert
        verify(passwordEncoder).encode("password");
        verify(userService).inscription(any(UserDTO.class));
    }

    @Test
    @DisplayName("POST /user/inscription returns 409 when the user already exists")
    void inscription_existingUser_returnsConflict() throws Exception {
        UserDTO request = new UserDTO(null, "Jane", "Doe", "jane@example.com", "password", Role.STUDENT, "MAT123");
        UserDTO existingUser = new UserDTO(1L, "Jane", "Doe", request.getEmail(), null, Role.STUDENT, "MAT123");

        when(userService.getUserByEmail(request.getEmail())).thenReturn(existingUser);

        mockMvc.perform(post("/user/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("POST /user/inscription returns 400 for an unknown role")
    void inscription_unknownRole_returnsBadRequest() throws Exception {
        String request = """
                {
                  "firstName": "Jane",
                  "lastname": "Doe",
                  "email": "jane@example.com",
                  "password": "password",
                  "role": "UNKNOWN",
                  "matricule": "MAT123"
                }
                """;

        mockMvc.perform(post("/user/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
