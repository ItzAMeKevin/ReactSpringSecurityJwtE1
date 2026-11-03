package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.OffrerException;
import com.lacouf.rsbjwt.mapper.CvMetaDataMapper;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.repository.NotificationRepository;


/////////////////////                                WTF????                  ////////////////////////////////
@Service
@RequiredArgsConstructor
public class StudentCvService {
    private static final int MAX_CV_SIZE_BYTES = 5 * 1024 * 1024;
    private static final byte[] PDF_SIGNATURE = {'%', 'P', 'D', 'F', '-'};
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final StudentCvRepository studentCvRepository;
    private final StudentRepository studentRepository;
    private final JobOfferRepository jobOfferRepository;
    private final NotificationRepository notificationRepository;
    private final CvMetaDataMapper cvMetaDataMapper;

    @Transactional
    public CvMetaDataDto upload(String authenticatedEmail, CvUploadDto request) {
        validatePdfContentType(request.getContentType());
        byte[] decodedContent = decodeBase64AndValidatePdf(request.getContent());
        Student student = findStudentByEmail(authenticatedEmail);
        StudentCv savedCv = createAndSaveStudentCv(student, request.getFileName(), decodedContent);

        this.notificationRepository.save(Notification.builder()
                .message("notification.types.CV_UPLOADED.message")
                .user(student)
                .notificationType(NotificationType.CV)
                .referenceId(savedCv.getId())
                .build());

        return cvMetaDataMapper.toDto(savedCv);
    }

    private Student findStudentByEmail(String email) {
        return studentRepository.findByCredentialsEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validatePdfContentType(String contentType) {
        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new InvalidCvException("errors.pdfOnly");
        }
    }

    private byte[] decodeBase64AndValidatePdf(String base64Content) {
        byte[] content;
        try {
            content = Base64.getDecoder().decode(base64Content);
        } catch (IllegalArgumentException e) {
            throw new InvalidCvException("errors.invalidBase64");
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
        cv.setStatus(Status.PENDING);
        cv.setStudent(student);

        return studentCvRepository.save(cv);
    }

    private static boolean isPdfSignatureValid(byte[] content) {
        if (content.length < PDF_SIGNATURE.length) {
            return false;
        }
        return Arrays.equals(content, 0, PDF_SIGNATURE.length, PDF_SIGNATURE, 0, PDF_SIGNATURE.length);
    }

    private void validatePdfContent(byte[] content) {
        if (content.length == 0 || content.length > MAX_CV_SIZE_BYTES) {
            throw new InvalidCvException("errors.cvSize");
        }
        if (!isPdfSignatureValid(content)) {
            throw new InvalidCvException("errors.invalidPdf");
        }
    }

    public String getReviewFile(Long cvId, String authenticatedEmail) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("errors.cvNotFound"));

        Student student = findStudentByEmail(authenticatedEmail);
        if (!cv.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("errors.accessDenied");
        }

        return cv.getReviewFeedback();
    }

    @Transactional(readOnly = true)
    public byte[] getCvContent(Long cvId, String authenticatedEmail) {
        Student student = findStudentByEmail(authenticatedEmail);
        return studentCvRepository.findByIdAndStudent_Id(cvId, student.getId())
                .orElseThrow(() -> new InvalidCvException("errors.cvNotFound"))
                .getContent();
    }

    private static String sanitizeFileName(String fileName) {
        String normalized = fileName.replace('\\', '_').replace('/', '_').trim();
        if (normalized.isEmpty() || normalized.length() > 255) {
            throw new InvalidCvException("errors.invalidFileName");
        }
        return normalized;
    }

    @Transactional
    public List<CvMetaDataDto> listCvs(String authenticatedEmail) {
        findStudentByEmail(authenticatedEmail);
        return  cvMetaDataMapper.toDtoList(studentCvRepository.findByStudentCredentialsEmailOrderByUploadedAtDesc(authenticatedEmail));
    }


    @Transactional
    public void applyToJobOffer(long offerID, CvMetaDataDto cvDto) throws CvException, OffrerException {
        JobOffer jobOffer = jobOfferRepository.findById(offerID).orElseThrow(() -> new OffrerException("errors.offerNotFound"));

        List<StudentCv> appliedCvs = jobOffer.getAppliedCvs();

        if (appliedCvs.stream().anyMatch(cv -> cv.getId().equals(cvDto.getId()))) {
            throw new OffrerException("errors.cvAlreadyApplied");
        } else {
            StudentCv studentCv = studentCvRepository.findById(cvDto.getId())
                    .orElseThrow(() -> new CvException("errors.cvNotFound"));
            appliedCvs.add(studentCv);
            jobOffer.setAppliedCvs(appliedCvs);
            jobOfferRepository.save(jobOffer);
        }
    }
}
