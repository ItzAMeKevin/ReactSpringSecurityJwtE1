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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.Optional;
import java.util.List;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentCvRepository studentCvRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private byte[] validPdfContent;

    @BeforeEach
    void setUp() {
        // ARRANGE
        testStudent = new Student();
        testStudent.setId(1L);

        validPdfContent = new byte[]{37, 80, 68, 70, 45, 49, 46, 52};
    }

    @Test
    void testValidPdfSuccess() {
        // ARRANGE
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        UploadCvDto request = new UploadCvDto("cv.pdf", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.save(any(StudentCv.class)))
                .thenAnswer(invocation -> {
                    StudentCv cv = invocation.getArgument(0);
                    cv.setId(1L);
                    return cv;
                });

        // ACT
        CvMetaDataDto result = studentService.upload(email, request);

        // ASSERT
        assertNotNull(result);
        assertEquals("cv.pdf", result.getFileName());
        assertEquals("application/pdf", result.getContentType());
        assertEquals(CvStatus.PENDING, result.getStatus());
        verify(studentCvRepository, times(1)).save(any(StudentCv.class));
    }

    @Test
    void testInvalidContentTypeThrows() {
        // ARRANGE
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        UploadCvDto request = new UploadCvDto("cv.pdf", "text/plain", base64Content);

        // ACT & ASSERT
        InvalidCvException exception = assertThrows(InvalidCvException.class, () -> {
            studentService.upload(email, request);
        });
        assertEquals("Seuls les PDF sont acceptés.", exception.getMessage());
        verify(studentCvRepository, never()).save(any());
    }

    @Test
    void testInvalidBase64Throws() {
        // ARRANGE
        String email = "student@test.com";
        UploadCvDto request = new UploadCvDto("cv.pdf", "application/pdf", "not@valid@base64!!!");

        // ACT & ASSERT
        InvalidCvException exception = assertThrows(InvalidCvException.class, () -> {
            studentService.upload(email, request);
        });
        assertEquals("Le contenu Base64 est invalide.", exception.getMessage());
        verify(studentCvRepository, never()).save(any());
    }

    @Test
    void testEmptyFileThrows() {
        // ARRANGE
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(new byte[]{});
        UploadCvDto request = new UploadCvDto("cv.pdf", "application/pdf", base64Content);

        // ACT & ASSERT
        InvalidCvException exception = assertThrows(InvalidCvException.class, () -> {
            studentService.upload(email, request);
        });
        assertEquals("Le fichier doit faire entre 1 octet et 5 Mo.", exception.getMessage());
    }

    @Test
    void testInvalidSignatureThrows () {
        // ARRANGE
        String email = "student@test.com";
        byte[] invalidPdfContent = new byte[]{1, 2, 3, 4, 5}; // Pas %PDF-
        String base64Content = Base64.getEncoder().encodeToString(invalidPdfContent);
        UploadCvDto request = new UploadCvDto("cv.pdf", "application/pdf", base64Content);

        // ACT & ASSERT
        InvalidCvException exception = assertThrows(InvalidCvException.class, () -> {
            studentService.upload(email, request);
        });
        assertEquals("Le contenu fourni n'est pas un PDF.", exception.getMessage());
    }

    @Test
    void testStudentNotFoundThrows() {
        // ARRANGE
        String email = "unknown@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        UploadCvDto request = new UploadCvDto("cv.pdf", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, () -> {
            studentService.upload(email, request);
        });
        verify(studentCvRepository, never()).save(any());
    }

    @Test
    void testInvalidFileNameThrows() {
        // ARRANGE
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        UploadCvDto request = new UploadCvDto("", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));

        // ACT & ASSERT
        InvalidCvException exception = assertThrows(InvalidCvException.class, () -> {
            studentService.upload(email, request);
        });
        assertEquals("Le nom de fichier est invalide.", exception.getMessage());
    }

    @Test
    void testPathTraversalFileNameSanitized() {
        // ARRANGE
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        UploadCvDto request = new UploadCvDto("..\\..\\evil.pdf", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.save(any(StudentCv.class)))
                .thenAnswer(invocation -> {
                    StudentCv cv = invocation.getArgument(0);
                    cv.setId(1L);
                    return cv;
                });

        // ACT
        CvMetaDataDto result = studentService.upload(email, request);

        // ASSERT
        assertEquals(".._.._evil.pdf", result.getFileName());
        verify(studentCvRepository, times(1)).save(any(StudentCv.class));
    }

    @Test
    void testListCvsSuccess() {
        // ARRANGE
        String email = "student@test.com";

        StudentCv cv1 = new StudentCv();
        cv1.setId(1L);
        cv1.setFileName("cv_recent.pdf");
        cv1.setContentType("application/pdf");
        cv1.setSize(1024);
        cv1.setStatus(CvStatus.PENDING);
        cv1.setUploadedAt(Instant.now());
        cv1.setStudent(testStudent);

        StudentCv cv2 = new StudentCv();
        cv2.setId(2L);
        cv2.setFileName("cv_ancien.pdf");
        cv2.setContentType("application/pdf");
        cv2.setSize(2048);
        cv2.setStatus(CvStatus.ACCEPTED);
        cv2.setUploadedAt(Instant.now().minusSeconds(3600));
        cv2.setStudent(testStudent);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.findByStudentCredentialsEmailOrderByUploadedAtDesc(email))
                .thenReturn(List.of(cv1, cv2));

        // ACT
        List<CvMetaDataDto> result = studentService.listCvs(email);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("cv_recent.pdf", result.get(0).getFileName());
        assertEquals(CvStatus.PENDING, result.get(0).getStatus());
        assertEquals("cv_ancien.pdf", result.get(1).getFileName());
        assertEquals(CvStatus.ACCEPTED, result.get(1).getStatus());
        verify(studentCvRepository, times(1)).findByStudentCredentialsEmailOrderByUploadedAtDesc(email);
    }

    @Test
    void testListCvsEmptyWhenNoneUploaded() {
        // ARRANGE
        String email = "student@test.com";

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.findByStudentCredentialsEmailOrderByUploadedAtDesc(email))
                .thenReturn(List.of());

        // ACT
        List<CvMetaDataDto> result = studentService.listCvs(email);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testListCvsStudentNotFoundThrows() {
        // ARRANGE
        String email = "unknown@test.com";

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, () -> {
            studentService.listCvs(email);
        });
        verify(studentCvRepository, never()).findByStudentCredentialsEmailOrderByUploadedAtDesc(any());
    }
}