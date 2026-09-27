package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.CvStatus;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import com.lacouf.rsbjwt.service.dto.UploadCvDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class StudentCvService {
    private static final int MAX_CV_SIZE_BYTES = 5 * 1024 * 1024;
    private static final byte[] PDF_SIGNATURE = {'%', 'P', 'D', 'F', '-'};
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final StudentCvRepository studentCvRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public CvMetaDataDto upload(String authenticatedEmail, UploadCvDto request) {
        validatePdfContentType(request.getContentType());
        byte[] decodedContent = decodeBase64AndValidatePdf(request.getContent());
        Student student = findStudentByEmail(authenticatedEmail);
        StudentCv savedCv = createAndSaveStudentCv(student, request.getFileName(), decodedContent);
        return CvMetaDataDto.toCvMetaDataDto(savedCv);
    }

    private Student findStudentByEmail(String email) {
        return studentRepository.findByCredentialsEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validatePdfContentType(String contentType) {
        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new InvalidCvException("Seuls les PDF sont acceptés.");
        }
    }

    private byte[] decodeBase64AndValidatePdf(String base64Content) {
        byte[] content;
        try {
            content = Base64.getDecoder().decode(base64Content);
        } catch (IllegalArgumentException e) {
            throw new InvalidCvException("Le contenu Base64 est invalide.");
        }
        validatePdfContent(content);
        return content;
    }

        private StudentCv createAndSaveStudentCv(Student student, String fileName, byte[] content) {
        StudentCv cv = new StudentCv();
        cv.setFileName(sanitizeFileName(fileName));
        cv.setContentType(PDF_CONTENT_TYPE);
        cv.setContent(content);
        cv.setSize(content.length);
        cv.setStatus(CvStatus.PENDING);
        cv.setStudent(student);

        return studentCvRepository.save(cv);
    }

    private static boolean isPdfSignatureValid(byte[] content) {
        if (content.length < PDF_SIGNATURE.length) {
            return false;
        }
        return Arrays.equals(content, 0 , PDF_SIGNATURE.length, PDF_SIGNATURE, 0, PDF_SIGNATURE.length);
    }

    private void validatePdfContent(byte[] content){
        if (content.length == 0 || content.length > MAX_CV_SIZE_BYTES) {
            throw new InvalidCvException("Le fichier doit faire entre 1 octet et 5 Mo.");
        }
        if (!isPdfSignatureValid(content)) {
            throw new InvalidCvException("Le contenu fourni n'est pas un PDF.");
        }
    }

    private static String sanitizeFileName(String fileName) {
        String normalized = fileName.replace('\\', '_').replace('/', '_').trim();
        if (normalized.isEmpty() || normalized.length() > 255) {
            throw new InvalidCvException("Le nom de fichier est invalide.");
        }
        return normalized;
    }
}
