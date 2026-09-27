package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.EmployerDto;
import com.lacouf.rsbjwt.service.dto.ManagerDto;
import com.lacouf.rsbjwt.service.dto.StudentDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {


    private final UserAppService UserAppService;


    public ReactSpringSecurityJwtApplication(UserAppService UserService) {

        this.UserAppService = UserService;

    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        if (UserAppService.getUserByEmail("l@l.com") == null)
            UserAppService.inscription(
                    ManagerDto.builder()
                            .firstName("Gerard")
                            .lastName("Biblio")
                            .matricule("PROF-001")
                            .email("l@l.com")
                            .role(Role.MANAGER)
                            .password("bib")
                            .build()
            );

        if (UserAppService.getUserByEmail("ll@l.com") == null)
            UserAppService.inscription(
                    StudentDto.builder()
                            .firstName("Isidor")
                            .lastName("Teurteur")
                            .matricule("ETUD-001")
                            .email("ll@l.com")
                            .password("bib")
                            .role(Role.STUDENT)
                            .programe(Programe.SOINS_INFIRMIERS)
                            .build()
            );

        if (UserAppService.getUserByEmail("lll@l.com") == null)
            UserAppService.inscription(
                    EmployerDto.builder()
                            .firstName("Chandeuse")
                            .lastName("Lixor")
                            .email("lll@l.com")
                            .role(Role.EMPLOYER)
                            .companyName("Lixor Inc.")
                            .address("123 rue Principale")
                            .postalCode("H0H 0H0")
                            .city("Montréal")
                            .phoneNumber("514-555-1234")
                            .employerId("EMP-001")
                            .password("bib")
                            .build()
            );
    }

}

