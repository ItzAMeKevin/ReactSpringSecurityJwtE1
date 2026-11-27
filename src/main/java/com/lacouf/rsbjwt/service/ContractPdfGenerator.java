package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Adresse;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.Student;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

final class ContractPdfGenerator {
    private static final float PAGE_WIDTH = PDRectangle.LETTER.getWidth();
    private static final float PAGE_TOP_BASELINE = 750;
    private static final float MARGIN = 28;
    private static final float CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2;
    private static final PDFont REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ContractPdfGenerator() {
    }

    static byte[] generate(JobOffer offer, Student student, Employer employer, Manager manager) throws IOException {
        String studentName = fullName(student.getFirstName(), student.getLastName(), "student");
        String employerName = required(employer.getCompanyName(), "employer");
        String managerName = fullName(manager.getFirstName(), manager.getLastName(), "manager");
        String jobTitle = required(offer.getTitle(), "job offer title");
        String description = required(offer.getDescription(), "job description");
        String salary = required(offer.getSalary(), "hourly salary");
        if (offer.getStartingDate() == null || offer.getDurationInWeeks() < 1) {
            throw new IllegalArgumentException("The job offer must have a start date and a positive duration");
        }
        String address = formatAddress(offer.getAdresse());
        String startDate = DATE_FORMAT.format(offer.getStartingDate());
        String endDate = DATE_FORMAT.format(offer.getStartingDate().plusWeeks(offer.getDurationInWeeks()));

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            drawCover(document, studentName, jobTitle);
            drawTerms(document, managerName, employerName, studentName, address, startDate, endDate,
                    offer.getDurationInWeeks(), salary);
            drawDutiesAndSignatures(document, description, studentName, employerName, managerName);
            document.save(output);
            return output.toByteArray();
        }
    }

    private static void drawCover(PDDocument document, String studentName, String jobTitle) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            centered(stream, "CONTRAT DE STAGE", BOLD, 26, 500);
            centered(stream, jobTitle, REGULAR, 16, 300);
            centered(stream, studentName, REGULAR, 14, 270);
        }
    }

    private static void drawTerms(
            PDDocument document,
            String managerName,
            String employerName,
            String studentName,
            String address,
            String startDate,
            String endDate,
            int durationInWeeks,
            String salary) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            centered(stream, "ENTENTE DE STAGE INTERVENUE ENTRE LES PARTIES SUIVANTES", BOLD, 14, 750);
            centered(stream, "Dans le cadre de la formule ATE, les parties citées ci-dessous :", REGULAR, 12, 704);
            centered(stream, "Le gestionnaire de stage, " + managerName, REGULAR, 12, 678);
            centered(stream, "et", BOLD, 12, 644);
            centered(stream, "L'employeur, " + employerName, REGULAR, 12, 610);
            centered(stream, "et", BOLD, 12, 576);
            centered(stream, "L'étudiant(e), " + studentName, REGULAR, 12, 542);
            centered(stream, "Conviennent des conditions de stage suivantes :", REGULAR, 12, 500);

            float top = 470;
            top = drawSection(stream, "ENDROIT DU STAGE", List.of("Adresse : " + address), top);
            top = drawSection(stream, "DUREE DU STAGE", List.of(
                    "Date de début : " + startDate,
                    "Date de fin : " + endDate,
                    "Nombre total de semaines : " + durationInWeeks), top);
            drawSection(stream, "SALAIRE", List.of("Salaire horaire : " + salary), top);
        }
    }

    private static void drawDutiesAndSignatures(
            PDDocument document,
            String description,
            String studentName,
            String employerName,
            String managerName) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        float currentBaselineY;
        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            currentBaselineY = PAGE_TOP_BASELINE;
            centered(stream, "TACHES ET RESPONSABILITES DU STAGIAIRE", BOLD, 14, currentBaselineY);
            currentBaselineY = drawWrappedText(stream, description, REGULAR, 12, MARGIN,
                    currentBaselineY - 36, CONTENT_WIDTH, 16);
            currentBaselineY -= 18;
        }

        if (currentBaselineY < 370) {
            page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            currentBaselineY = PAGE_TOP_BASELINE;
        }
        try (PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
            drawResponsibilitiesAndSignatures(stream, studentName, employerName, managerName, currentBaselineY);
        }
    }

    private static void drawResponsibilitiesAndSignatures(
            PDPageContentStream stream,
            String studentName,
            String employerName,
            String managerName,
            float currentBaselineY) throws IOException {
        centered(stream, "RESPONSABILITES", BOLD, 14, currentBaselineY);
        currentBaselineY -= 30;
        currentBaselineY = drawWrappedText(stream, "Le Collège s'engage à : ...", REGULAR, 12, MARGIN,
                currentBaselineY, CONTENT_WIDTH, 16) - 10;
        currentBaselineY = drawWrappedText(stream, "L'entreprise s'engage à : ...", REGULAR, 12, MARGIN,
                currentBaselineY, CONTENT_WIDTH, 16) - 10;
        currentBaselineY = drawWrappedText(stream, "L'étudiant s'engage à : ...", REGULAR, 12, MARGIN,
                currentBaselineY, CONTENT_WIDTH, 16) - 22;

        centered(stream, "SIGNATURES", BOLD, 14, currentBaselineY);
        currentBaselineY -= 26;
        currentBaselineY = drawWrappedText(stream, "Les parties s'engagent a respecter cette entente de stage.",
                REGULAR, 11, MARGIN, currentBaselineY, CONTENT_WIDTH, 15) - 8;
        currentBaselineY = drawWrappedText(stream, "En foi de quoi les parties ont signé :", REGULAR, 11,
                MARGIN, currentBaselineY, CONTENT_WIDTH, 15) - 14;

        currentBaselineY = drawSignatureBlock(stream, "L'étudiant(e)", studentName, currentBaselineY);
        currentBaselineY = drawSignatureBlock(stream, "L'employeur", employerName, currentBaselineY);
        drawSignatureBlock(stream, "Le gestionnaire de stage", managerName, currentBaselineY);
    }

    private static float drawSection(PDPageContentStream stream, String heading, List<String> lines, float top)
            throws IOException {
        float headingHeight = 24;
        float bodyHeight = Math.max(34, 18 * lines.size() + 10);
        float left = MARGIN;
        float width = CONTENT_WIDTH;

        stream.setNonStrokingColor(new Color(230, 230, 230));
        stream.addRect(left, top - headingHeight, width, headingHeight);
        stream.fill();
        stream.setStrokingColor(Color.BLACK);
        stream.addRect(left, top - headingHeight - bodyHeight, width, headingHeight + bodyHeight);
        stream.stroke();
        text(stream, heading, BOLD, 13, left + 8, top - 17);
        float lineBaselineY = top - headingHeight - 17;
        for (String line : lines) {
            text(stream, line, REGULAR, 12, left + 8, lineBaselineY);
            lineBaselineY -= 18;
        }
        return top - headingHeight - bodyHeight;
    }

    private static float drawSignatureBlock(
            PDPageContentStream stream, String role, String name, float roleBaselineY)
            throws IOException {
        text(stream, role + " :", BOLD, 11, MARGIN, roleBaselineY);
        text(stream, "Signature : ______________________________    Date : ______________", REGULAR, 10,
                MARGIN, roleBaselineY - 18);
        text(stream, name, REGULAR, 10, MARGIN, roleBaselineY - 34);
        return roleBaselineY - 54;
    }

    private static float drawWrappedText(
            PDPageContentStream stream,
            String value,
            PDFont font,
            float fontSize,
            float x,
            float firstBaselineY,
            float maxWidth,
            float lineHeight) throws IOException {
        float currentBaselineY = firstBaselineY;
        for (String line : wrap(value, font, fontSize, maxWidth)) {
            text(stream, line, font, fontSize, x, currentBaselineY);
            currentBaselineY -= lineHeight;
        }
        return currentBaselineY;
    }

    private static List<String> wrap(String value, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : value.trim().split("\\s+")) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (font.getStringWidth(candidate) * fontSize / 1000 <= maxWidth || line.length() == 0) {
                line.setLength(0);
                line.append(candidate);
            } else {
                lines.add(line.toString());
                line.setLength(0);
                line.append(word);
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private static void centered(
            PDPageContentStream stream, String value, PDFont font, float size, float baselineY)
            throws IOException {
        float width = font.getStringWidth(value) * size / 1000;
        text(stream, value, font, size, Math.max(MARGIN, (PAGE_WIDTH - width) / 2), baselineY);
    }

    private static void text(
            PDPageContentStream stream, String value, PDFont font, float size, float x, float baselineY)
            throws IOException {
        stream.beginText();
        stream.setFont(font, size);
        stream.newLineAtOffset(x, baselineY);
        stream.showText(value);
        stream.endText();
    }

    private static String formatAddress(Adresse address) {
        if (address == null) {
            throw new IllegalArgumentException("The job offer must have a workplace address");
        }
        String street = required(address.getNumeroCivic(), "civic number")
                + " " + required(address.getRue(), "street");
        String city = required(address.getVille(), "city")
                + " " + required(address.getCodePostal(), "postal code");
        String country = address.getPay();
        return street + ", " + city
                + (country == null || country.isBlank() ? "" : ", " + country.trim());
    }

    private static String fullName(String firstName, String lastName, String role) {
        return required(firstName, role + " first name") + " " + required(lastName, role + " last name");
    }

    private static String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing " + fieldName + " required to generate contract");
        }
        return value.trim();
    }
}
