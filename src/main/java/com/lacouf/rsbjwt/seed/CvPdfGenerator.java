package com.lacouf.rsbjwt.seed;

import java.nio.charset.StandardCharsets;
import java.util.List;

final class CvPdfGenerator {

    private CvPdfGenerator() {}

    static byte[] generate(CvContent cv) {
        return wrapInPdf(buildContentStream(cv));
    }

    private static String buildContentStream(CvContent cv) {
        StringBuilder s = new StringBuilder();
        int y = 740;
        y = line(s, cv.name(), 14, 72, y) - 20;
        y = line(s, cv.contact(), 9, 72, y) - 24;
        y = section(s, "FORMATION", cv.education(), y);
        y = section(s, "COMPETENCES", cv.skills(), y);
        section(s, "EXPERIENCE PROFESSIONNELLE", cv.experience(), y);
        return s.toString();
    }

    private static int section(StringBuilder s, String title, List<String> lines, int y) {
        y = line(s, title, 11, 72, y) - 16;
        for (String text : lines) {
            y = line(s, text, 9, 82, y) - 14;
        }
        return y - 8;
    }

    private static int line(StringBuilder s, String text, int fontSize, int x, int y) {
        String safe = text.replaceAll("[^\\x20-\\x7E]", "?")
                .replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
        s.append("BT /F1 ").append(fontSize).append(" Tf ")
         .append(x).append(" ").append(y).append(" Td (")
         .append(safe).append(") Tj ET\n");
        return y;
    }

    private static byte[] wrapInPdf(String stream) {
        byte[] streamBytes = stream.getBytes(StandardCharsets.US_ASCII);
        String pdf = "%PDF-1.4\n"
                + "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n"
                + "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n"
                + "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792]"
                + " /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n"
                + "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n"
                + "5 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n"
                + stream
                + "endstream\nendobj\n"
                + "xref\n0 6\n0000000000 65535 f \n"
                + "trailer\n<< /Size 6 /Root 1 0 R >>\n%%EOF";
        return pdf.getBytes(StandardCharsets.US_ASCII);
    }
}
