package com.lacouf.rsbjwt.service;

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

    public String authenticateUser(LoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        final String token = jwtTokenProvider.generateToken(authentication);
        System.out.println("JWT Token " + token);
        return token;
    }

    public UserDTO getMe(String token) {
        token = token.startsWith("Bearer") ? token.substring(7) : token;
        String email = jwtTokenProvider.getEmailFromJWT(token);
        User user = userAppRepository.findUserAppByEmail(email).orElseThrow(UserNotFoundException::new);
        return switch(user.getRole()){
            case STUDENT -> getEmprunteurDto(user.getId());
            case EMPLOYER -> getPreposeDto(user.getId());
            case MANAGER -> getManagerDto(user.getId());
        };
    }
    
    public UserDTO inscription(UserDTO userDTO) {
        User user = userDTO.toEntity(userDTO);
        final User savedUser = userAppRepository.save(user);
        return UserDTO.toUserDTO(savedUser);
    }

    public boolean matriculeExists(String matricule) {
        return studentRepository.findByMatricule(matricule).isPresent()
                || managerRepository.findByMatricule(matricule).isPresent();
    }

    public boolean employerIdExists(String employerId) {
        return employerRepository.findByEmployerId(employerId).isPresent();
    }

    public UserDTO getUserByEmail(String email) {
        final Optional<User> userOptional = userAppRepository.findUserAppByEmail(email);
        return userOptional.isPresent() ?
                UserDTO.toUserDTO(userOptional.get()) :
                null;
    }

    private ManagerDto getManagerDto(Long id) {
        final Optional<Manager> managerOptional = managerRepository.findById(id);
        return managerOptional.isPresent() ?
                ManagerDto.toManagerDto(managerOptional.get()) :
                ManagerDto.empty();
    }

    private EmployerDto getPreposeDto(Long id) {
        final Optional<Employer> preposeOptional = employerRepository.findById(id);
        return preposeOptional.isPresent() ?
                EmployerDto.toEmployerDto(preposeOptional.get()) :
                EmployerDto.empty();
    }

    private StudentDto getEmprunteurDto(Long id) {
        final Optional<Student> emprunteurOptional = studentRepository.findById(id);
        return emprunteurOptional.isPresent() ?
                StudentDto.toStudentDto(emprunteurOptional.get()) :
                StudentDto.empty();
    }
}
