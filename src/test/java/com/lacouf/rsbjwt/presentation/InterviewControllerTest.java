package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.ApplicationNotFoundException;
import com.lacouf.rsbjwt.model.InterviewType;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.JwtAuthenticationEntryPoint;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.security.SecurityConfiguration;
import com.lacouf.rsbjwt.service.InterviewService;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.InterviewResponseDto;
import com.lacouf.rsbjwt.service.dto.InterviewSlotDto;
import com.lacouf.rsbjwt.service.dto.ScheduleInterviewRequestDto;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterviewController.class)
@Import(SecurityConfiguration.class)
@ActiveProfiles("test")
class InterviewControllerTest {

    @MockitoBean
    private UserAppService userAppService;

    @MockitoBean
    private InterviewService interviewService;

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
    private ScheduleInterviewRequestDto validRequest;
    private InterviewResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        validRequest = new ScheduleInterviewRequestDto(
                1L,
                InterviewType.ONLINE,
                "https://zoom.us/j/123",
                null,
                "Bring portfolio",
                List.of(new InterviewSlotDto(
                        LocalDate.of(2026, 11, 20),
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0)))
        );

        responseDto = new InterviewResponseDto(
                10L, 1L, InterviewType.ONLINE,
                "https://zoom.us/j/123", null, "Bring portfolio",
                Instant.now(),
                List.of(new InterviewSlotDto(
                        LocalDate.of(2026, 11, 20),
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0)))
        );
    }

    @Test
    void schedule_success_returns201() throws Exception {
        when(interviewService.schedule(any(ScheduleInterviewRequestDto.class), anyString()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/interview/schedule")
                        .with(employer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.candidatureId").value(1L))
                .andExpect(jsonPath("$.type").value("ONLINE"));

        verify(interviewService).schedule(any(), anyString());
    }

    @Test
    void schedule_wrongRole_returns403() throws Exception {
        mockMvc.perform(post("/interview/schedule")
                        .with(user("student@test.com").authorities(new SimpleGrantedAuthority("STUDENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRequest)))
                .andExpect(status().isForbidden());

        verify(interviewService, never()).schedule(any(), any());
    }

    @Test
    void schedule_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/interview/schedule")
                        .with(employer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(interviewService, never()).schedule(any(), any());
    }

    @Test
    void schedule_notFound_returns404() throws Exception {
        when(interviewService.schedule(any(ScheduleInterviewRequestDto.class), anyString()))
                .thenThrow(new ApplicationNotFoundException());

        mockMvc.perform(post("/interview/schedule")
                        .with(employer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());
    }

    private static RequestPostProcessor employer() {
        return user("marie@acme.com").authorities(new SimpleGrantedAuthority("EMPLOYER"));
    }
}