package com.lacouf.rsbjwt.seed;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

import static com.lacouf.rsbjwt.seed.UserSeeder.CMRC_EMAIL;
import static com.lacouf.rsbjwt.seed.UserSeeder.HOTEL_EMAIL;
import static com.lacouf.rsbjwt.seed.UserSeeder.NEXUS_EMAIL;

@Component
@RequiredArgsConstructor
class JobOfferSeeder {

    static final String NEXUS_FULLSTACK_OFFER_TITLE = "Développeur full-stack Java/React";
    static final String NEXUS_BACKEND_OFFER_TITLE = "Développeur back-end Spring Boot";
    static final String NEXUS_FRONTEND_OFFER_TITLE = "Développeur front-end React";
    static final String NEXUS_SUPPORT_OFFER_TITLE = "Technicien support TI";
    static final String CMRC_OFFER_TITLE = "Stagiaire technicien en génie civil";
    static final String HOTEL_OFFER_TITLE = "Stagiaire à la réception et aux opérations";

    private final EmployerRepository employerRepository;
    private final JobOfferRepository jobOfferRepository;

    void seed() {
        seedOffer(NEXUS_EMAIL, Status.ACCEPTED, JobOffer.builder()
                .title(NEXUS_FULLSTACK_OFFER_TITLE)
                .description("Participez au développement d'une application web moderne au sein de notre équipe de 12 développeurs. " +
                        "Vous contribuerez à la conception d'API REST avec Spring Boot et à la création d'interfaces " +
                        "utilisateur dynamiques avec React. Travail en méthodologie Agile (Scrum, sprints de 2 semaines).")
                .prerequisites("Java, Spring Boot, React, Git. Bases de données relationnelles (SQL). " +
                        "Atout : connaissance de Docker et des tests unitaires.")
                .salary("22")
                .startingDate(LocalDate.of(2027, 2, 1))
                .durationInWeeks(15)
                .numberOfPositions(1)
                .programe(Programe.TECHNIQUES_INFORMATIQUE));

        seedOffer(NEXUS_EMAIL, Status.PENDING, JobOffer.builder()
                .title(NEXUS_BACKEND_OFFER_TITLE)
                .description("Rejoignez notre équipe infrastructure pour concevoir et maintenir des microservices haute performance. " +
                        "Vous travaillerez sur la sécurité des API (JWT, OAuth2), l'optimisation de requêtes SQL " +
                        "et la mise en place de pipelines CI/CD. Mentorat assuré par un développeur senior.")
                .prerequisites("Spring Boot, REST, JWT, PostgreSQL, Maven. " +
                        "Connaissance des tests d'intégration (JUnit, Testcontainers) un atout.")
                .salary("20")
                .startingDate(LocalDate.of(2027, 1, 4))
                .durationInWeeks(15)
                .numberOfPositions(1)
                .programe(Programe.TECHNIQUES_INFORMATIQUE));

        seedOffer(NEXUS_EMAIL, Status.PENDING, JobOffer.builder()
                .title(NEXUS_FRONTEND_OFFER_TITLE)
                .description("Intégrez notre équipe produit pour développer des interfaces React accessibles et performantes. " +
                        "Vous collaborerez avec les designers UX et participerez aux revues de code.")
                .prerequisites("React, TypeScript, HTML/CSS, Git. Atout : Vitest ou Jest.")
                .salary("21")
                .startingDate(LocalDate.of(2027, 1, 4))
                .durationInWeeks(15)
                .numberOfPositions(1)
                .programe(Programe.TECHNIQUES_INFORMATIQUE));

        seedRefusedOffer(NEXUS_EMAIL,
                "Description trop vague : précisez les tâches techniques (ex. Active Directory, "
                        + "gestion du parc informatique), l'horaire de travail et l'encadrement offert.",
                JobOffer.builder()
                .title(NEXUS_SUPPORT_OFFER_TITLE)
                .description("Offrez un soutien de premier niveau aux utilisateurs, gérez les tickets et participez " +
                        "au déploiement de postes de travail.")
                .prerequisites("Notions de réseaux, Windows/Linux, bon sens du service à la clientèle.")
                .salary("19")
                .startingDate(LocalDate.of(2027, 1, 11))
                .durationInWeeks(12)
                .numberOfPositions(1)
                .programe(Programe.TECHNIQUES_INFORMATIQUE));

        seedOffer(CMRC_EMAIL, Status.ACCEPTED, JobOffer.builder()
                .title(CMRC_OFFER_TITLE)
                .description("Le département de génie civil du Collège militaire royal du Canada accueille un stagiaire " +
                        "technicien à son laboratoire. Vous participerez aux essais sur les matériaux (béton, granulats, sols), " +
                        "à la préparation des montages pour les projets de recherche et aux relevés d'arpentage sur le campus.")
                .prerequisites("Être inscrit en Technologie du génie civil (221.B0). Compétences : AutoCAD, essais de " +
                        "laboratoire, lecture de plans et devis. Atout : bilinguisme français-anglais.")
                .salary("21")
                .startingDate(LocalDate.of(2027, 5, 3))
                .durationInWeeks(16)
                .numberOfPositions(2)
                .programe(Programe.TECHNIQUE_GENIE_CIVIL));

        seedOffer(HOTEL_EMAIL, Status.ACCEPTED, JobOffer.builder()
                .title(HOTEL_OFFER_TITLE)
                .description("Intégrez l'équipe de la réception d'un hôtel-boutique de 120 chambres dans le Vieux-Montréal. "
                        + "Vous participerez à l'accueil des clients, aux arrivées et départs, à la gestion des réservations "
                        + "dans Opera PMS et à la coordination avec l'entretien ménager.")
                .prerequisites("Être inscrit en Techniques de gestion hôtelière. Bilinguisme français-anglais. "
                        + "Atout : expérience en service à la clientèle.")
                .salary("18")
                .startingDate(LocalDate.of(2027, 1, 18))
                .durationInWeeks(12)
                .numberOfPositions(2)
                .programe(Programe.TECHNIQUES_GESTION_HOTELIERE));
    }

    Optional<JobOffer> findOffer(String employerEmail, String title) {
        return jobOfferRepository.getJobOffersByEmployerId(findEmployer(employerEmail).getId())
                .stream()
                .filter(offer -> offer.getTitle().equals(title))
                .findFirst();
    }

    private void seedOffer(String employerEmail, Status status, JobOffer.JobOfferBuilder builder) {
        saveIfAbsent(employerEmail, status, null, builder);
    }

    private void seedRefusedOffer(String employerEmail, String refusalFeedback, JobOffer.JobOfferBuilder builder) {
        saveIfAbsent(employerEmail, Status.DECLINED, refusalFeedback, builder);
    }

    private void saveIfAbsent(String employerEmail, Status status, String refusalFeedback,
                              JobOffer.JobOfferBuilder builder) {
        JobOffer offer = builder.build();
        if (findOffer(employerEmail, offer.getTitle()).isPresent()) return;
        Employer employer = findEmployer(employerEmail);
        offer.setEmployer(employer);
        offer.setAdresse(employer.getAdresse());
        offer.setStatus(status);
        offer.setRefusalFeedback(refusalFeedback);
        jobOfferRepository.save(offer);
    }

    private Employer findEmployer(String email) {
        return employerRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seed: employeur introuvable " + email));
    }
}
