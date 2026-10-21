package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
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

        // --- Seed users (emails/passwords unchanged for team compatibility) ---

        if (userAppService.getUserByEmail("l@l.com") == null) {
            ManagerCreateDto manager = new ManagerCreateDto();
            manager.setFirstName("Marie");
            manager.setLastname("Leclerc");
            manager.setMatricule("PROF-4521");
            manager.setEmail("l@l.com");
            manager.setRole(Role.MANAGER);
            manager.setPassword(passwordEncoder.encode("bib"));
            userAppService.inscription(manager);
        }

        if (userAppService.getUserByEmail("ll@l.com") == null) {
            StudentCreateDto student = new StudentCreateDto();
            student.setFirstName("Sophie");
            student.setLastname("Tremblay");
            student.setMatricule("2441532");
            student.setEmail("ll@l.com");
            student.setPassword(passwordEncoder.encode("bib"));
            student.setRole(Role.STUDENT);
            student.setPrograme(Programe.SOINS_INFIRMIERS);
            userAppService.inscription(student);
        }

        if (userAppService.getUserByEmail("lll@l.com") == null) {
            EmployerCreateDto employer = new EmployerCreateDto();
            employer.setFirstName("Jean-Philippe");
            employer.setLastname("Bouchard");
            employer.setEmail("lll@l.com");
            employer.setRole(Role.EMPLOYER);
            employer.setCompanyName("Nexus Technologies inc.");
            employer.setPhoneNumber("514-555-9234");
            employer.setEmployerWorkId("NXT-7821");
            employer.setPassword(passwordEncoder.encode("bib"));

            AdresseDTO adresse = new AdresseDTO();
            adresse.setPays("Canada");
            adresse.setVille("Montreal");
            adresse.setRue("4120 boul. Saint-Laurent");
            adresse.setNumeroCivic("4120");
            adresse.setCodePostal("H2W 1Y7");
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

        // --- Seed job offers ---
        // ACCEPTED: immediately visible to the student in the demo
        seedJobOffer(jobOffers, employer,
                "Developpeur full-stack Java/React",
                "Participez au developpement d'une application web moderne au sein de notre equipe de 12 developpeurs. " +
                "Vous contribuerez a la conception d'APIs REST avec Spring Boot et a la creation d'interfaces " +
                "utilisateur dynamiques avec React. Travail en methodologie Agile (Scrum, sprints de 2 semaines).",
                "Java, Spring Boot, React, Git. Bases de donnees relationnelles (SQL). " +
                "Atout: connaissance de Docker et des tests unitaires.",
                "22$/h",
                LocalDate.of(2026, 5, 4), 15, Status.ACCEPTED);

        // WAITING: the manager reviews and accepts this one during the demo
        seedJobOffer(jobOffers, employer,
                "Developpeur back-end Spring Boot",
                "Rejoignez notre equipe infrastructure pour concevoir et maintenir des microservices haute performance. " +
                "Vous travaillerez sur la securite des APIs (JWT, OAuth2), l'optimisation de requetes SQL, " +
                "et la mise en place de pipelines CI/CD. Mentorat assure par un developpeur senior.",
                "Spring Boot, REST, JWT, PostgreSQL, Maven. " +
                "Connaissance des tests d'integration (JUnit, Testcontainers) un atout.",
                "20$/h",
                LocalDate.of(2026, 9, 1), 15, Status.WAITING);

        // --- Seed CVs ---
        // ACCEPTED: student already has an accepted CV so the offers list is unlocked on login
        seedCv(studentCvs, student, "cv-sophie-web.pdf", Status.ACCEPTED,
                buildCvStream(
                        "Sophie Tremblay",
                        "ll@l.com  |  514-555-0192  |  Montreal, QC",
                        new String[]{
                                "Techniques de l'informatique - Cegep Andre-Laurendeau (2024 - present)",
                                "DEC prevu: juin 2026  |  Cote R: 32.4"
                        },
                        new String[]{
                                "Langages: Java, JavaScript, TypeScript, Python, SQL",
                                "Frameworks: Spring Boot, React, Angular, Node.js",
                                "Outils: Git, Docker, IntelliJ IDEA, VS Code, Postman",
                                "Bases de donnees: PostgreSQL, MySQL"
                        },
                        new String[]{
                                "Stage - Developpeur Web  |  WebSolutions QC, Montreal  |  Jan-Avr 2025",
                                "  Creation d'interfaces React responsive et accessibles (WCAG 2.1)",
                                "  Integration d'APIs REST avec authentification JWT",
                                "  Tests unitaires avec Vitest et React Testing Library",
                                "Projet academique - Systeme de gestion de stages (2025)",
                                "  Application full-stack: React + Spring Security + PostgreSQL",
                                "  Authentification par roles (etudiant, employeur, gestionnaire)"
                        }
                )
        );

        // PENDING: the manager reviews and accepts/declines this one during the demo
        seedCv(studentCvs, student, "cv-sophie-backend.pdf", Status.PENDING,
                buildCvStream(
                        "Sophie Tremblay",
                        "ll@l.com  |  514-555-0192  |  Montreal, QC",
                        new String[]{
                                "Techniques de l'informatique - Cegep Andre-Laurendeau (2024 - present)",
                                "DEC prevu: juin 2026  |  Cote R: 32.4"
                        },
                        new String[]{
                                "Langages: Java, Python, SQL, Bash",
                                "Frameworks: Spring Boot, Spring Security, Hibernate, JPA",
                                "Outils: Git, Docker, Maven, Postman, IntelliJ IDEA",
                                "Bases de donnees: PostgreSQL, MySQL, Redis"
                        },
                        new String[]{
                                "Stage - Developpeur Java  |  Logiciels ProSoft, Laval  |  Jan-Avr 2025",
                                "  Conception de microservices avec Spring Boot",
                                "  Optimisation de requetes SQL sur base PostgreSQL (gain de performance 40%)",
                                "  Mise en place de tests d'integration avec Testcontainers",
                                "Projet academique - API de gestion des ressources humaines (2024)",
                                "  Spring Boot + JWT + PostgreSQL, deploye sur Docker",
                                "  Documentation Swagger / OpenAPI 3.0"
                        }
                )
        );
    }

    // ---- Seed helpers ----

    private void seedJobOffer(JobOfferRepository repo, Employer employer, String title,
                              String description, String prerequisites, String salary,
                              LocalDate startingDate, int durationInWeeks, Status status) {
        boolean alreadyExists = repo.getJobOffersByEmployerId(employer.getId())
                .stream()
                .anyMatch(offer -> offer.getTitle().equals(title));
        if (alreadyExists) return;

        JobOffer offer = JobOffer.builder()
                .title(title)
                .description(description)
                .prerequisites(prerequisites)
                .salary(salary)
                .startingDate(startingDate)
                .durationInWeeks(durationInWeeks)
                .programe(Programe.TECHNIQUES_INFORMATIQUE)
                .employer(employer)
                .adresse(new Adresse("Canada", "Montreal", "4120 boul. Saint-Laurent", "4120", "H2W 1Y7"))
                .build();
        offer.setStatus(status);
        repo.save(offer);
    }

    private void seedCv(StudentCvRepository repo, Student student, String fileName,
                        Status status, String streamContent) {
        boolean alreadyExists = repo.findAllByStudent_IdOrderByUploadedAtDesc(student.getId())
                .stream()
                .anyMatch(cv -> cv.getFileName().equals(fileName));
        if (alreadyExists) return;

        byte[] content = wrapPdfStream(streamContent);
        StudentCv cv = new StudentCv();
        cv.setFileName(fileName);
        cv.setContentType("application/pdf");
        cv.setContent(content);
        cv.setSize(content.length);
        cv.setStatus(status);
        cv.setStudent(student);
        repo.save(cv);
    }

    // ---- PDF builders ----

    private String buildCvStream(String name, String contact,
                                  String[] education, String[] skills, String[] experience) {
        StringBuilder s = new StringBuilder();
        int y = 740;

        pdfLine(s, name,    14, 72, y); y -= 20;
        pdfLine(s, contact,  9, 72, y); y -= 24;

        pdfLine(s, "FORMATION",   11, 72, y); y -= 16;
        for (String e : education)   { pdfLine(s, e,  9, 82, y); y -= 14; }
        y -= 8;

        pdfLine(s, "COMPETENCES", 11, 72, y); y -= 16;
        for (String sk : skills)     { pdfLine(s, sk, 9, 82, y); y -= 14; }
        y -= 8;

        pdfLine(s, "EXPERIENCE PROFESSIONNELLE", 11, 72, y); y -= 16;
        for (String ex : experience) { pdfLine(s, ex, 9, 82, y); y -= 14; }

        return s.toString();
    }

    private void pdfLine(StringBuilder s, String text, int fontSize, int x, int y) {
        String safe = text.replaceAll("[^\\x20-\\x7E]", "?");
        s.append("BT /F1 ").append(fontSize).append(" Tf ")
         .append(x).append(" ").append(y).append(" Td (")
         .append(safe.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)"))
         .append(") Tj ET\n");
    }

    private byte[] wrapPdfStream(String streamContent) {
        byte[] streamBytes = streamContent.getBytes(StandardCharsets.US_ASCII);
        String pdf = "%PDF-1.4\n"
                + "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n"
                + "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n"
                + "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792]"
                + " /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n"
                + "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n"
                + "5 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n"
                + streamContent
                + "endstream\nendobj\n"
                + "xref\n0 6\n0000000000 65535 f \n"
                + "trailer\n<< /Size 6 /Root 1 0 R >>\n%%EOF";
        return pdf.getBytes(StandardCharsets.US_ASCII);
    }
}
