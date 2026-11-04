package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.UserMapper;
import com.lacouf.rsbjwt.model.User;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.service.dto.LoginDTO;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAppServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider tokenProvider;
    @Mock private UserAppRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private EmployerRepository employerRepository;
    @Mock private ManagerRepository managerRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private Authentication authentication;
    @InjectMocks private UserAppService service;

    @Test
    void authenticatesAndReadsCurrentUser() {
        LoginDTO login = new LoginDTO("user@test.com", "password");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("token");
        assertEquals("token", service.authenticateUser(login));

        User user = mock(User.class);
        UserCreateDTO dto = UserCreateDTO.builder().email("user@test.com").build();
        when(tokenProvider.getEmailFromJWT("token")).thenReturn(" USER@TEST.COM ");
        when(userRepository.findUserAppByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);
        assertEquals(dto, service.getMe("Bearer token"));
        assertEquals(dto, service.getMe("token"));
    }

    @Test
    void rejectsUnknownCurrentUserAndMapsInscription() {
        when(tokenProvider.getEmailFromJWT("token")).thenReturn("missing@test.com");
        when(userRepository.findUserAppByEmail("missing@test.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getMe("token"));

        UserCreateDTO input = UserCreateDTO.builder().email("new@test.com").build();
        User user = mock(User.class);
        when(userMapper.toEntity(input)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(input);
        assertEquals(input, service.inscription(input));
    }

    @Test
    void checksMatriculesEmployersAndEmailLookup() {
        when(studentRepository.findByMatricule("S1")).thenReturn(Optional.empty());
        when(managerRepository.findByMatricule("S1")).thenReturn(Optional.of(mock(com.lacouf.rsbjwt.model.Manager.class)));
        assertTrue(service.matriculeExists("S1"));

        when(studentRepository.findByMatricule("S2")).thenReturn(Optional.empty());
        when(managerRepository.findByMatricule("S2")).thenReturn(Optional.empty());
        assertFalse(service.matriculeExists("S2"));

        when(employerRepository.findByEmployerWorkId("E1")).thenReturn(Optional.of(mock(com.lacouf.rsbjwt.model.Employer.class)));
        assertTrue(service.employerIdExists("E1"));

        when(userRepository.findUserAppByEmail("user@test.com")).thenReturn(Optional.empty());
        assertNull(service.getUserByEmail("user@test.com"));
    }
}
