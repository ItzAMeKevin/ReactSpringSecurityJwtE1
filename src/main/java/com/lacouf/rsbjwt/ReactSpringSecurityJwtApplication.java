package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.EmployerCreateDto;
import com.lacouf.rsbjwt.service.dto.ManagerCreateDto;
import com.lacouf.rsbjwt.service.dto.StudentCreateDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {


    private final UserAppService userAppService;
    private final PasswordEncoder passwordEncoder;

    public ReactSpringSecurityJwtApplication(UserAppService UserService, PasswordEncoder passwordEncoder) {
        this.userAppService = UserService;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        if (userAppService.getUserByEmail("l@l.com") == null) {
            ManagerCreateDto manager = new ManagerCreateDto();
            manager.setFirstName("Gerard");
            manager.setLastname("Biblio");
            manager.setMatricule("PROF-001");
            manager.setEmail("l@l.com");
            manager.setRole(Role.MANAGER);
            manager.setPassword(passwordEncoder.encode("bib"));
            userAppService.inscription(manager);
        }

        if (userAppService.getUserByEmail("ll@l.com") == null) {
            StudentCreateDto student = new StudentCreateDto();
            student.setFirstName("Isidor");
            student.setLastname("Teurteur");
            student.setMatricule("ETUD-001");
            student.setEmail("ll@l.com");
            student.setPassword(passwordEncoder.encode("bib"));
            student.setRole(Role.STUDENT);
            student.setPrograme(Programe.SOINS_INFIRMIERS);
            userAppService.inscription(student);
        }

        if (userAppService.getUserByEmail("lll@l.com") == null) {
            EmployerCreateDto employer = new EmployerCreateDto();
            employer.setFirstName("Chandeuse");
            employer.setLastname("Lixor");
            employer.setEmail("lll@l.com");
            employer.setRole(Role.EMPLOYER);
            employer.setCompanyName("Lixor Inc.");
            employer.setPhoneNumber("514-555-1234");
            employer.setEmployerWorkId("EMP-001");
            employer.setPassword(passwordEncoder.encode("bib"));


            AdresseDTO adresse = new AdresseDTO();
            adresse.setPays("Canada");
            adresse.setVille("Montréal");
            adresse.setRue("123 rue Principale");
            adresse.setNumeroCivic("123");
            adresse.setCodePostal("H0H 0H0");
            employer.setAdresseDTO(adresse);

            userAppService.inscription(employer);
        }
    }
}

