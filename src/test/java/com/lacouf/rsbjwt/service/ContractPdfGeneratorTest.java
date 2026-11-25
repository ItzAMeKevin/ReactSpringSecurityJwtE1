package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Adresse;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.Student;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractPdfGeneratorTest {
    @Test
    void generatesThreePagePdfWithContractDetailsAndWithoutUnavailableSchedule() throws Exception {
        JobOffer offer = new JobOffer();
        offer.setTitle("Software Developer Intern");
        offer.setDescription("Build and test web applications.");
        offer.setSalary("$22/hour");
        offer.setStartingDate(LocalDate.of(2026, 5, 4));
        offer.setDurationInWeeks(12);
        offer.setAdresse(Adresse.builder()
                .numeroCivic("123")
                .rue("Main Street")
                .ville("Montreal")
                .codePostal("H1A 1A1")
                .pays("Canada")
                .build());

        Student student = new Student();
        student.setFirstName("Alex");
        student.setLastName("Student");
        Employer employer = new Employer();
        employer.setCompanyName("Example Company");
        Manager manager = new Manager();
        manager.setFirstName("Morgan");
        manager.setLastName("Manager");

        byte[] pdf = ContractPdfGenerator.generate(offer, student, employer, manager);
        try (var document = Loader.loadPDF(pdf)) {
            String text = new PDFTextStripper().getText(document);
            assertTrue(document.getNumberOfPages() == 3);
            assertTrue(text.contains("Alex Student"));
            assertTrue(text.contains("Example Company"));
            assertTrue(text.contains("04/05/2026"));
            assertTrue(text.contains("27/07/2026"));
            assertTrue(text.contains("Build and test web applications."));
            assertFalse(text.contains("HORAIRE DE TRAVAIL"));
            assertFalse(text.contains("Nombre total d'heures"));
        }
    }
}
