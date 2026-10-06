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
import com.lacouf.rsbjwt.service.event.CvUploadedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentCvServiceTest {

    @Mock
    private StudentCvRepository studentCvRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentCvService studentCvService;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

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
        CvMetaDataDto result = studentCvService.upload(email, request);

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
            studentCvService.upload(email, request);
        });
        assertEquals("Seuls les PDF sont acceptés.", exception.getMessage());
        verify(studentCvRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
    @Test
    void testValidPdfPublishesCvUploadedEventWithSavedId() {
        // ARRANGE
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        UploadCvDto request = new UploadCvDto("cv.pdf", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.save(any(StudentCv.class)))
                .thenAnswer(invocation -> {
                    StudentCv cv = invocation.getArgument(0);
                    cv.setId(42L);
                    return cv;
                });

        // ACT
        studentCvService.upload(email, request);

        // ASSERT
        ArgumentCaptor<CvUploadedEvent> captor = ArgumentCaptor.forClass(CvUploadedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());
        assertEquals(42L, captor.getValue().cvId());
    }

    @Test
    void testInvalidBase64Throws() {
        // ARRANGE
        String email = "student@test.com";
        UploadCvDto request = new UploadCvDto("cv.pdf", "application/pdf", "not@valid@base64!!!");

        // ACT & ASSERT
        InvalidCvException exception = assertThrows(InvalidCvException.class, () -> {
            studentCvService.upload(email, request);
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
            studentCvService.upload(email, request);
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
            studentCvService.upload(email, request);
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
            studentCvService.upload(email, request);
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
            studentCvService.upload(email, request);
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
        CvMetaDataDto result = studentCvService.upload(email, request);

        // ASSERT
        assertEquals(".._.._evil.pdf", result.getFileName());
        verify(studentCvRepository, times(1)).save(any(StudentCv.class));
    }
    @Test
    void testGetReviewFileOwnerReturnsContent() {
        // ARRANGE
        String email = "student@test.com";
        StudentCv cv = new StudentCv();
        cv.setStudent(testStudent);
        cv.setReviewContent(new byte[]{1, 2, 3});

        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));
        when(studentRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(testStudent));

        // ACT
        byte[] result = studentCvService.getReviewFile(5L, email);

        // ASSERT
        assertArrayEquals(new byte[]{1, 2, 3}, result);
    }
    @Test
    void testGetReviewFileOtherStudentThrows() {
        // ARRANGE
        String email = "student@test.com";
        Student otherStudent = new Student();
        otherStudent.setId(99L);
        StudentCv cv = new StudentCv();
        cv.setStudent(otherStudent);

        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));
        when(studentRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(testStudent));

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> studentCvService.getReviewFile(5L, email));
    }

    @Test
    void testGetReviewFileUnknownCvThrows() {
        // ARRANGE
        when(studentCvRepository.findById(5L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
                () -> studentCvService.getReviewFile(5L, "student@test.com"));
    }
}