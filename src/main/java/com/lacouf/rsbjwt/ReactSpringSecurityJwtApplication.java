package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.EmprunteurRepository;
import com.lacouf.rsbjwt.repository.GestionnaireRepository;
import com.lacouf.rsbjwt.repository.PreposeRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootApplication
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {

    private final GestionnaireRepository gestionnaireRepository;
    private final EmprunteurRepository emprunteurRepository;
    private final PreposeRepository preposeRepository;
    private final UserAppRepository userAppRepository;

    private final PasswordEncoder passwordEncoder;

    public ReactSpringSecurityJwtApplication(GestionnaireRepository gestionnaireRepository, EmprunteurRepository emprunteurRepository, PreposeRepository preposeRepository, UserAppRepository userAppRepository, PasswordEncoder passwordEncoder) {
        this.gestionnaireRepository = gestionnaireRepository;
        this.emprunteurRepository = emprunteurRepository;
        this.preposeRepository = preposeRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        gestionnaireRepository.save(
                SystemManager.builder()
                        .firstName("Gerard")
                        .lastName("Biblio")
                        .email("l@l.com")
                        .password(passwordEncoder.encode("bib"))
                        .build()
        );
        emprunteurRepository.save(
                Employer.builder()
                        .firstName("Isidor")
                        .lastName("Teurteur")
                        .email("ll@l.com")
                        .password(passwordEncoder.encode("bib"))
                        .build()
        );
        preposeRepository.save(
                Professor.builder()
                        .firstName("Chandeuse")
                        .lastName("Lixor")
                        .email("lll@l.com")
                        .password(passwordEncoder.encode("bib"))
                        .build()
        );
        final Optional<User> userAppByEmail = userAppRepository.findUserAppByEmail("l@l.com");
        userAppByEmail.ifPresent(userApp -> System.out.println("user " + userAppByEmail));

    }
}
