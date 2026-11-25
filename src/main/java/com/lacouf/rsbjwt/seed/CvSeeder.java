package com.lacouf.rsbjwt.seed;

import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.lacouf.rsbjwt.seed.UserSeeder.*;

@Component
@RequiredArgsConstructor
class CvSeeder {

    static final String ALICE_CV = "cv-alice.pdf";
    static final String BRUNO_CV = "cv-bruno.pdf";
    static final String CLARA_CV = "cv-clara.pdf";
    static final String LUCAS_DECLINED_CV = "cv-lucas-v1.pdf";
    static final String LUCAS_CV = "cv-lucas.pdf";
    static final String MIKA_CV = "cv-mika.pdf";
    static final String JAIME_CV = "cv-jaime.pdf";
    static final String SOPHIE_WEB_CV = "cv-sophie-web.pdf";
    static final String SOPHIE_BACKEND_CV = "cv-sophie-backend.pdf";

    private final StudentRepository studentRepository;
    private final StudentCvRepository studentCvRepository;

    void seed() {
        seedRefusedCv(LUCAS_EMAIL, LUCAS_DECLINED_CV,
                "CV trop sommaire : détaillez vos compétences techniques et ajoutez au moins un projet "
                        + "avec vos réalisations concrètes. Vérifiez aussi l'orthographe.",
                new CvContent(
                        "Lucas Gagnon",
                        contact(LUCAS_EMAIL, "514-555-0148", "Montreal, QC"),
                        List.of("Techniques de l'informatique - Cegep Andre-Laurendeau"),
                        List.of("Programation, informatique"),
                        List.of("Commis - Supermarche  (2025)")));

        seedCv(LUCAS_EMAIL, LUCAS_CV, Status.ACCEPTED, new CvContent(
                "Lucas Gagnon",
                contact(LUCAS_EMAIL, "514-555-0148", "Montreal, QC"),
                List.of("Techniques de l'informatique (420.B0) - Cegep Andre-Laurendeau  |  2024-2027 (en cours)"),
                List.of("Langages: Java, JavaScript, SQL", "Outils: Git, Docker, Postman"),
                List.of("Projet academique - Application de gestion de stages (2025)")));

        seedCv(MIKA_EMAIL, MIKA_CV, Status.ACCEPTED, new CvContent(
                "Mika Amara",
                contact(MIKA_EMAIL, "514-555-0163", "Montreal, QC"),
                List.of("Techniques de gestion hoteliere (430.A0) - ITHQ, Montreal  |  2024-2027 (en cours)"),
                List.of("Logiciels: Opera PMS, Maestro PMS, Microsoft Office",
                        "Reservations, accueil et service a la clientele",
                        "Langues: francais, anglais (bilingue)"),
                List.of("Preposee a la reception - Hotel Le Riverain, Montreal  (ete 2025)",
                        "  Arrivees et departs, gestion des reservations, encaissement",
                        "Serveuse - Restaurant Le Quai, Montreal  (2024)",
                        "  Service aux tables, prise de commandes, fermeture de caisse")));

        seedCv(JAIME_EMAIL, JAIME_CV, Status.ACCEPTED, new CvContent(
                "Jaime Laguerre",
                contact(JAIME_EMAIL, "514-555-0271", "Montreal, QC"),
                List.of("Technologie du genie civil (221.B0) - Cegep Andre-Laurendeau  |  2024-2027 (en cours)"),
                List.of("Logiciels: AutoCAD, Civil 3D, SAP2000",
                        "Materiaux: beton arme, charpente metallique, sol"),
                List.of("Projet academique - Conception d'un pont a poutrelles (2025)",
                        "  Calcul des charges, dimensionnement des elements porteurs",
                        "  Realisation des plans d'execution sous AutoCAD")));

        seedCv(ALICE_EMAIL, ALICE_CV, Status.ACCEPTED, new CvContent(
                "Alice Bouchard",
                contact(ALICE_EMAIL, "514-555-0101", "Montreal, QC"),
                List.of("Technologie du genie civil (221.B0) - Cegep Andre-Laurendeau  |  2024-2027 (en cours)"),
                List.of("AutoCAD Civil 3D, Total Station Leica, GPS RTK",
                        "Essais de beton (ASTM C143, C39), analyse granulometrique",
                        "Microsoft Office, lecture de plans et devis"),
                List.of("Aide-technicienne - Construction Belanger Inc.  (ete 2026)",
                        "  Releves topographiques, controle qualite du beton, suivi des as-built",
                        "Assistante technique - Arrondissement Rosemont  (ete 2025)",
                        "  Inspection de trottoirs, bordures et regards, saisie dans Excel")));

        seedCv(BRUNO_EMAIL, BRUNO_CV, Status.ACCEPTED, new CvContent(
                "Bruno Lacroix",
                contact(BRUNO_EMAIL, "438-555-0202", "Laval, QC"),
                List.of("Technologie du genie civil (221.B0) - College Montmorency  |  2024-2027 (en cours)"),
                List.of("AutoCAD 2D/3D, MicroStation, Civil 3D (notions)",
                        "Essais Proctor, granulometrie, limites d'Atterberg",
                        "Trimble Access, Microsoft Project, devis CCDG"),
                List.of("Technicien junior - Routek Infrastructures Inc.  (ete 2026)",
                        "  Inspection ouvrages routiers, collecte de donnees avec tablette Trimble",
                        "Manoeuvre specialise - Ville de Laval  (ete 2025)",
                        "  Rehabilitation de conduites d'aqueduc, pose de bordures en beton")));

        seedCv(CLARA_EMAIL, CLARA_CV, Status.ACCEPTED, new CvContent(
                "Clara Morin",
                contact(CLARA_EMAIL, "514-555-0303", "Montreal, QC"),
                List.of("Technologie du genie civil (221.B0) - Cegep Andre-Laurendeau  |  2024-2027 (en cours)"),
                List.of("AutoCAD Civil 3D, Revit (BIM niveau 2), Robot Structural Analysis",
                        "Codes: CNBC 2020, NQ 3660-500, CSA A23.3 (beton), CSA S16 (acier)",
                        "Essais NDT (sclerometre, pachometre), Bluebeam Revu"),
                List.of("Assistante en bureau d'etudes - Groupe Conseil Rive-Sud  (ete 2026)",
                        "  Mise a jour de plans sous AutoCAD, calcul de charges (neige, vent, sismique)",
                        "Stagiaire en verification - Bureau CBCQ  (ete 2025)",
                        "  Inspection de batiments residentiels, rapports avec photos annotees")));

        seedCv(SOPHIE_EMAIL, SOPHIE_WEB_CV, Status.PENDING, new CvContent(
                "Sophie Tremblay",
                contact(SOPHIE_EMAIL, "514-555-0192", "Montreal, QC"),
                List.of("Techniques de l'informatique (420.B0) - Cegep Andre-Laurendeau  |  2024-2027 (en cours)",
                        "DEC prevu: juin 2027  |  Cote R: 32.4"),
                List.of("Langages: Java, JavaScript, TypeScript, Python, SQL",
                        "Frameworks: Spring Boot, React, Angular, Node.js",
                        "Outils: Git, Docker, IntelliJ IDEA, VS Code, Postman",
                        "Bases de donnees: PostgreSQL, MySQL"),
                List.of("Emploi d'ete - Developpeur Web junior  |  WebSolutions QC, Montreal  |  Ete 2026",
                        "  Creation d'interfaces React responsive et accessibles (WCAG 2.1)",
                        "  Integration d'APIs REST avec authentification JWT",
                        "  Tests unitaires avec Vitest et React Testing Library",
                        "Projet academique - Systeme de gestion de stages (2025)",
                        "  Application full-stack: React + Spring Security + PostgreSQL",
                        "  Authentification par roles (etudiant, employeur, gestionnaire)")));

        seedCv(SOPHIE_EMAIL, SOPHIE_BACKEND_CV, Status.PENDING, new CvContent(
                "Sophie Tremblay",
                contact(SOPHIE_EMAIL, "514-555-0192", "Montreal, QC"),
                List.of("Techniques de l'informatique (420.B0) - Cegep Andre-Laurendeau  |  2024-2027 (en cours)",
                        "DEC prevu: juin 2027  |  Cote R: 32.4"),
                List.of("Langages: Java, Python, SQL, Bash",
                        "Frameworks: Spring Boot, Spring Security, Hibernate, JPA",
                        "Outils: Git, Docker, Maven, Postman, IntelliJ IDEA",
                        "Bases de donnees: PostgreSQL, MySQL, Redis"),
                List.of("Emploi d'ete - Developpeur Java junior  |  Logiciels ProSoft, Laval  |  Ete 2026",
                        "  Conception de microservices avec Spring Boot",
                        "  Optimisation de requetes SQL sur base PostgreSQL (gain de performance 40%)",
                        "  Mise en place de tests d'integration avec Testcontainers",
                        "Projet academique - API de gestion des ressources humaines (2025)",
                        "  Spring Boot + JWT + PostgreSQL, deploye sur Docker",
                        "  Documentation Swagger / OpenAPI 3.0")));
    }

    Optional<StudentCv> findCv(String email, String fileName) {
        return findCv(findStudent(email), fileName);
    }

    private Optional<StudentCv> findCv(Student student, String fileName) {
        return studentCvRepository.findAllByStudent_IdOrderByUploadedAtDesc(student.getId())
                .stream()
                .filter(cv -> cv.getFileName().equals(fileName))
                .findFirst();
    }

    private void seedCv(String email, String fileName, Status status, CvContent content) {
        saveIfAbsent(email, fileName, status, null, content);
    }

    private void seedRefusedCv(String email, String fileName, String feedback, CvContent content) {
        saveIfAbsent(email, fileName, Status.DECLINED, feedback, content);
    }

    private void saveIfAbsent(String email, String fileName, Status status, String feedback, CvContent content) {
        Student student = findStudent(email);
        if (findCv(student, fileName).isPresent()) return;
        byte[] pdf = CvPdfGenerator.generate(content);
        StudentCv cv = new StudentCv();
        cv.setStudent(student);
        cv.setFileName(fileName);
        cv.setContentType("application/pdf");
        cv.setContent(pdf);
        cv.setSize(pdf.length);
        cv.setStatus(status);
        cv.setReviewFeedback(feedback);
        studentCvRepository.save(cv);
    }

    private Student findStudent(String email) {
        return studentRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seed: etudiant introuvable " + email));
    }

    private static String contact(String email, String phone, String city) {
        return email + "  |  " + phone + "  |  " + city;
    }
}
