package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.Programe;
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

        UserAppService.inscription(
                ManagerDto.builder()
                        .firstName("Gerard")
                        .lastName("Biblio")
                        .matricule("PROF-001")
                        .email("l@l.com")
                        .password("bib")
                        .build()
        );


        UserAppService.inscription(
                StudentDto.builder()
                        .firstName("Isidor")
                        .lastName("Teurteur")
                        .matricule("ETUD-001")
                        .email("ll@l.com")
                        .password("bib")
                        .programe(Programe.SOINS_INFIRMIERS)
                        .build()
        );


        UserAppService.inscription(
                EmployerDto.builder()
                        .firstName("Chandeuse")
                        .lastName("Lixor")
                        .email("lll@l.com")
                        .password("bib")
                        .build()
        );
    }

}

