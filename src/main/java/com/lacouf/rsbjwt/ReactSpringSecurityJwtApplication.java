package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {

    private final ManagerRepository managerRepository;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;
    private final UserAppRepository userAppRepository;

    private final PasswordEncoder passwordEncoder;

    public ReactSpringSecurityJwtApplication(ManagerRepository managerRepository, StudentRepository emprunteurRepository, EmployerRepository employerRepository, UserAppRepository userAppRepository, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.studentRepository = emprunteurRepository;
        this.employerRepository = employerRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (userAppRepository.findUserAppByEmail("l@l.com").isEmpty()) {
            managerRepository.save(
                Manager.builder()
                    .firstName("Gerard")
                    .lastName("Biblio")
                    .matricule("PROF-001")
                    .email("l@l.com")
                    .password(passwordEncoder.encode("bib"))
                    .build()
            );
        }
        if (userAppRepository.findUserAppByEmail("ll@l.com").isEmpty()) {
            studentRepository.save(
                Student.builder()
                    .firstName("Isidor")
                    .lastName("Teurteur")
                    .matricule("ETUD-001")
                    .email("ll@l.com")
                    .password(passwordEncoder.encode("bib"))
                    .build()
            );
        }
        if (userAppRepository.findUserAppByEmail("lll@l.com").isEmpty()) {
            employerRepository.save(
                Employer.builder()
                    .firstName("Chandeuse")
                    .lastName("Lixor")
                    .email("lll@l.com")
                    .password(passwordEncoder.encode("bib"))
                    .build()
            );
        }
        final Optional<User> userAppByEmail = userAppRepository.findUserAppByEmail("l@l.com");
        userAppByEmail.ifPresent(userApp -> System.out.println("user " + userAppByEmail));

    }
}
