package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.UserMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAppService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAppRepository userAppRepository;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;



    public String authenticateUser(LoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        final String token = jwtTokenProvider.generateToken(authentication);
        System.out.println("JWT Token " + token);
        return token;
    }

    public UserCreateDTO getMe(String token) {
        token = token.startsWith("Bearer") ? token.substring(7) : token;
        String email = jwtTokenProvider.getEmailFromJWT(token);
        User user = userAppRepository.findUserAppByEmail(email).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);

    }

    @Transactional
    public UserCreateDTO inscription(UserCreateDTO userCreateDTO) {
        User user = userMapper.toEntity(userCreateDTO);
        final User savedUser = userAppRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public boolean matriculeExists(String matricule) {
        return studentRepository.findByMatricule(matricule).isPresent()
                || managerRepository.findByMatricule(matricule).isPresent();
    }

    public boolean employerIdExists(String employerId) {
        return employerRepository.findByEmployerWorkId(employerId).isPresent();
    }

    public UserCreateDTO getUserByEmail(String email) {
        final Optional<User> userOptional = userAppRepository.findUserAppByEmail(email);
        return userOptional.map(userMapper::toDto).orElse(null);
    }

}
