package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.CvStatus;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import com.lacouf.rsbjwt.service.event.CvUploadedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
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
        testStudent = new Student();
        testStudent.setId(1L);

        validPdfContent = new byte[]{37, 80, 68, 70, 45, 49, 46, 52};
    }

    @Test
    void testValidPdfSuccess() {
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        CvUploadDto request = new CvUploadDto("cv.pdf", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.save(any(StudentCv.class)))
                .thenAnswer(invocation -> {
                    StudentCv cv = invocation.getArgument(0);
                    cv.setId(1L);
                    return cv;
                });

        CvMetaDataDto result = studentCvService.upload(email, request);

        assertNotNull(result);
        assertEquals("cv.pdf", result.getFileName());
        assertEquals("application/pdf", result.getContentType());
        assertEquals(CvStatus.PENDING, result.getStatus());
        verify(studentCvRepository, times(1)).save(any(StudentCv.class));
    }

    @Test
    void testInvalidContentTypeThrows() {
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        CvUploadDto request = new CvUploadDto("cv.pdf", "text/plain", base64Content);

        InvalidCvException exception = assertThrows(InvalidCvException.class, () ->
                studentCvService.upload(email, request));
        assertEquals("Seuls les PDF sont acceptés.", exception.getMessage());
        verify(studentCvRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void testValidPdfPublishesCvUploadedEventWithSavedId() {
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        CvUploadDto request = new CvUploadDto("cv.pdf", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.save(any(StudentCv.class)))
                .thenAnswer(invocation -> {
                    StudentCv cv = invocation.getArgument(0);
                    cv.setId(42L);
                    return cv;
                });

        studentCvService.upload(email, request);

        ArgumentCaptor<CvUploadedEvent> captor = ArgumentCaptor.forClass(CvUploadedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());
        assertEquals(42L, captor.getValue().cvId());
    }

    @Test
    void testInvalidBase64Throws() {
        String email = "student@test.com";
        CvUploadDto request = new CvUploadDto("cv.pdf", "application/pdf", "not@valid@base64!!!");

        InvalidCvException exception = assertThrows(InvalidCvException.class, () ->
                studentCvService.upload(email, request));
        assertEquals("Le contenu Base64 est invalide.", exception.getMessage());
        verify(studentCvRepository, never()).save(any());
    }

    @Test
    void testEmptyFileThrows() {
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(new byte[]{});
        CvUploadDto request = new CvUploadDto("cv.pdf", "application/pdf", base64Content);

        InvalidCvException exception = assertThrows(InvalidCvException.class, () ->
                studentCvService.upload(email, request));
        assertEquals("Le fichier doit faire entre 1 octet et 5 Mo.", exception.getMessage());
    }

    @Test
    void testInvalidSignatureThrows() {
        String email = "student@test.com";
        byte[] invalidPdfContent = new byte[]{1, 2, 3, 4, 5};
        String base64Content = Base64.getEncoder().encodeToString(invalidPdfContent);
        CvUploadDto request = new CvUploadDto("cv.pdf", "application/pdf", base64Content);

        InvalidCvException exception = assertThrows(InvalidCvException.class, () ->
                studentCvService.upload(email, request));
        assertEquals("Le contenu fourni n'est pas un PDF.", exception.getMessage());
    }

    @Test
    void testStudentNotFoundThrows() {
        String email = "unknown@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        CvUploadDto request = new CvUploadDto("cv.pdf", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                studentCvService.upload(email, request));
        verify(studentCvRepository, never()).save(any());
    }

    @Test
    void testInvalidFileNameThrows() {
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        CvUploadDto request = new CvUploadDto("", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));

        InvalidCvException exception = assertThrows(InvalidCvException.class, () ->
                studentCvService.upload(email, request));
        assertEquals("Le nom de fichier est invalide.", exception.getMessage());
    }

    @Test
    void testPathTraversalFileNameSanitized() {
        String email = "student@test.com";
        String base64Content = Base64.getEncoder().encodeToString(validPdfContent);
        CvUploadDto request = new CvUploadDto("..\\..\\evil.pdf", "application/pdf", base64Content);

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.save(any(StudentCv.class)))
                .thenAnswer(invocation -> {
                    StudentCv cv = invocation.getArgument(0);
                    cv.setId(1L);
                    return cv;
                });

        CvMetaDataDto result = studentCvService.upload(email, request);

        assertEquals(".._.._evil.pdf", result.getFileName());
        verify(studentCvRepository, times(1)).save(any(StudentCv.class));
    }

    @Test
    void testListCvsSuccess() {
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

        List<CvMetaDataDto> result = studentCvService.listCvs(email);

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
        String email = "student@test.com";

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.of(testStudent));
        when(studentCvRepository.findByStudentCredentialsEmailOrderByUploadedAtDesc(email))
                .thenReturn(List.of());

        List<CvMetaDataDto> result = studentCvService.listCvs(email);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testListCvsStudentNotFoundThrows() {
        String email = "unknown@test.com";

        when(studentRepository.findByCredentialsEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                studentCvService.listCvs(email));
        verify(studentCvRepository, never()).findByStudentCredentialsEmailOrderByUploadedAtDesc(any());
    }

    @Test
    void testGetReviewFileOwnerReturnsContent() {
        String email = "student@test.com";
        StudentCv cv = new StudentCv();
        cv.setStudent(testStudent);
        cv.setReviewContent(new byte[]{1, 2, 3});

        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));
        when(studentRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(testStudent));

        byte[] result = studentCvService.getReviewFile(5L, email);

        assertArrayEquals(new byte[]{1, 2, 3}, result);
    }

    @Test
    void testGetReviewFileOtherStudentThrows() {
        String email = "student@test.com";
        Student otherStudent = new Student();
        otherStudent.setId(99L);
        StudentCv cv = new StudentCv();
        cv.setStudent(otherStudent);

        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));
        when(studentRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(testStudent));

        assertThrows(RuntimeException.class, () -> studentCvService.getReviewFile(5L, email));
    }

    @Test
    void testGetReviewFileUnknownCvThrows() {
        when(studentCvRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> studentCvService.getReviewFile(5L, "student@test.com"));
    }
}
