package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.model.Adresse;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.EmployerCreateDto;
import com.lacouf.rsbjwt.service.dto.ManagerCreateDto;
import com.lacouf.rsbjwt.service.dto.StudentCreateDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {


    private final UserAppService userAppService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JobOfferRepository> jobOfferRepository;
    private final ObjectProvider<EmployerRepository> employerRepository;
    private final ObjectProvider<StudentCvRepository> studentCvRepository;
    private final ObjectProvider<StudentRepository> studentRepository;

    public ReactSpringSecurityJwtApplication(UserAppService UserService, PasswordEncoder passwordEncoder,
                                             ObjectProvider<JobOfferRepository> jobOfferRepository,
                                             ObjectProvider<EmployerRepository> employerRepository,
                                             ObjectProvider<StudentCvRepository> studentCvRepository,
                                             ObjectProvider<StudentRepository> studentRepository) {
        this.userAppService = UserService;
        this.passwordEncoder = passwordEncoder;
        this.jobOfferRepository = jobOfferRepository;
        this.employerRepository = employerRepository;
        this.studentCvRepository = studentCvRepository;
        this.studentRepository = studentRepository;
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

        EmployerRepository employers = employerRepository.getIfAvailable();
        StudentRepository students = studentRepository.getIfAvailable();
        JobOfferRepository jobOffers = jobOfferRepository.getIfAvailable();
        StudentCvRepository studentCvs = studentCvRepository.getIfAvailable();
        if (employers == null || students == null || jobOffers == null || studentCvs == null) {
            return;
        }

        Employer employer = employers.findByCredentialsEmail("lll@l.com").orElse(null);
        Student student = students.findByCredentialsEmail("ll@l.com").orElse(null);
        if (employer == null || student == null) {
            return;
        }

        seedJobOffer(jobOffers, employer, "Stage en développement web",
                "Participer au développement et à la maintenance d'applications web.",
                "Java, Spring Boot et bases de données relationnelles.",
                LocalDate.of(2026, 5, 4), 15);
        seedJobOffer(jobOffers, employer, "Stage en développement logiciel",
                "Contribuer à la conception et aux tests de fonctionnalités logicielles.",
                "Connaissances en programmation orientée objet et Git.",
                LocalDate.of(2026, 9, 1), 15);

        seedCv(studentCvs, student, "cv-isidor-1.pdf", "Isidor Teurteur - CV développement web");
        seedCv(studentCvs, student, "cv-isidor-2.pdf", "Isidor Teurteur - CV développement logiciel");
    }

    private void seedJobOffer(JobOfferRepository jobOfferRepository, Employer employer, String title, String description,
                              String prerequisites, LocalDate startingDate, int durationInWeeks) {
        boolean alreadyExists = jobOfferRepository.getJobOffersByEmployerId(employer.getId())
                .stream()
                .anyMatch(offer -> offer.getTitle().equals(title));
        if (alreadyExists) {
            return;
        }

        JobOffer offer = JobOffer.builder()
                .title(title)
                .description(description)
                .prerequisites(prerequisites)
                .salary("À discuter")
                .startingDate(startingDate)
                .durationInWeeks(durationInWeeks)
                .programe(Programe.TECHNIQUES_INFORMATIQUE)
                .employer(employer)
                .adresse(new Adresse("Canada", "Montréal", "123 rue Principale", "123", "H0H 0H0"))
                .build();
        jobOfferRepository.save(offer);
    }

    private void seedCv(StudentCvRepository studentCvRepository, Student student, String fileName, String text) {
        boolean alreadyExists = studentCvRepository.findAllByStudent_IdOrderByUploadedAtDesc(student.getId())
                .stream()
                .anyMatch(cv -> cv.getFileName().equals(fileName));
        if (alreadyExists) {
            return;
        }

        byte[] content = createPdfContent(text);
        StudentCv cv = new StudentCv();
        cv.setFileName(fileName);
        cv.setContentType("application/pdf");
        cv.setContent(content);
        cv.setSize(content.length);
        cv.setStatus(Status.PENDING);
        cv.setStudent(student);
        studentCvRepository.save(cv);
    }

    private byte[] createPdfContent(String text) {
        String escapedText = text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
        String pdf = "%PDF-1.4\n"
                + "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n"
                + "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n"
                + "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792]"
                + " /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n"
                + "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n"
                + "5 0 obj\n<< /Length " + (escapedText.length() + 34) + " >>\nstream\n"
                + "BT /F1 12 Tf 72 720 Td (" + escapedText + ") Tj ET\nendstream\nendobj\n"
                + "xref\n0 6\n0000000000 65535 f \n"
                + "trailer\n<< /Size 6 /Root 1 0 R >>\n%%EOF";
        return pdf.getBytes(StandardCharsets.US_ASCII);
    }
}
