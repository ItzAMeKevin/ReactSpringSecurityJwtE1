package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.CvException;
import com.lacouf.rsbjwt.exception.OffrerException;
import com.lacouf.rsbjwt.mapper.CvMetaDataMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.InvalidCvException;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentCvServiceTest {

    @Mock private StudentCvRepository studentCvRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private JobOfferRepository jobOfferRepository;
    @Mock private SystemeService systemeService;
    @Mock private JobApplicationRepository jobApplicationRepository;
    @Spy private CvMetaDataMapper cvMetaDataMapper = Mappers.getMapper(CvMetaDataMapper.class);
    @InjectMocks private StudentCvService service;

    private final Student student = student(1L);
    private final byte[] pdf = new byte[]{'%', 'P', 'D', 'F', '-', '1'};

    private static Student student(Long id) {
        Student student = new Student();
        student.setId(id);
        return student;
    }

    private CvUploadDto uploadRequest(String fileName, byte[] content) {
        return new CvUploadDto(fileName, "application/pdf",
                Base64.getEncoder().encodeToString(content));
    }

    @Test
    void uploadsValidPdfAndNotifiesManagers() {
        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentCvRepository.save(any(StudentCv.class))).thenAnswer(invocation -> {
            StudentCv cv = invocation.getArgument(0);
            cv.setId(5L);
            return cv;
        });

        CvMetaDataDto result = service.upload("student@test.com", uploadRequest("cv.pdf", pdf));

        assertEquals("cv.pdf", result.getFileName());
        assertEquals(Status.PENDING, result.getStatus());
        verify(systemeService).notifyManagers(NotificationType.CV, 5L, "notification.types.CV_UPLOADED.message");
    }

    @Test
    void rejectsInvalidUploadInput() {
        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        assertThrows(InvalidCvException.class,
                () -> service.upload("student@test.com", new CvUploadDto("cv.pdf", "text/plain", "")));
        assertThrows(InvalidCvException.class,
                () -> service.upload("student@test.com", uploadRequest("cv.pdf", new byte[]{1, 2, 3})));
        assertThrows(InvalidCvException.class,
                () -> service.upload("student@test.com", uploadRequest("", pdf)));
        assertThrows(InvalidCvException.class,
                () -> service.upload("student@test.com",
                        new CvUploadDto("cv.pdf", "application/pdf", "not-base64!")));
    }

    @Test
    void rejectsUnknownStudent() {
        when(studentRepository.findByCredentialsEmail("missing@test.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> service.upload("missing@test.com", uploadRequest("cv.pdf", pdf)));
    }

    @Test
    void readsReviewAndContentWithOwnershipChecks() {
        StudentCv cv = new StudentCv();
        cv.setStudent(student);
        cv.setContent(pdf);
        cv.setReviewFeedback("Good");
        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));
        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentCvRepository.findByIdAndStudent_Id(5L, 1L)).thenReturn(Optional.of(cv));

        assertEquals("Good", service.getReviewFile(5L, "student@test.com"));
        assertArrayEquals(pdf, service.getCvContent(5L, "student@test.com"));
    }

    @Test
    void rejectsMissingOrUnauthorizedContent() {
        when(studentCvRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(InvalidCvException.class, () -> service.getReviewFile(5L, "student@test.com"));

        Student owner = student(2L);
        StudentCv cv = new StudentCv();
        cv.setStudent(owner);
        when(studentCvRepository.findById(5L)).thenReturn(Optional.of(cv));
        when(studentRepository.findByCredentialsEmail("student@test.com")).thenReturn(Optional.of(student));
        assertThrows(InvalidCvException.class, () -> service.getReviewFile(5L, "student@test.com"));
    }

    @Test
    void appliesCvAndRejectsDuplicateOrMissingData() throws Exception {
        JobOffer offer = new JobOffer();
        StudentCv cv = new StudentCv();
        cv.setId(4L);
        cv.setStatus(Status.ACCEPTED);
        when(jobOfferRepository.findById(2L)).thenReturn(Optional.of(offer));
        when(studentCvRepository.findById(4L)).thenReturn(Optional.of(cv));

        service.applyToJobOffer(2L, CvMetaDataDto.builder().id(4L).build());
        assertEquals(List.of(cv), offer.getAppliedCvs());
        verify(jobApplicationRepository).save(any(JobApplication.class));

        assertThrows(OffrerException.class,
                () -> service.applyToJobOffer(2L, CvMetaDataDto.builder().id(4L).build()));
        when(jobOfferRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(OffrerException.class,
                () -> service.applyToJobOffer(3L, CvMetaDataDto.builder().id(4L).build()));
        when(jobOfferRepository.findById(4L)).thenReturn(Optional.of(new JobOffer()));
        when(studentCvRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(CvException.class,
                () -> service.applyToJobOffer(4L, CvMetaDataDto.builder().id(4L).build()));
    }

    @ParameterizedTest(name = "un CV {0} ne peut pas postuler")
    @EnumSource(value = Status.class, names = "ACCEPTED", mode = EnumSource.Mode.EXCLUDE)
    void rejectsApplyingWithCvNotAccepted(Status cvStatus) {
        JobOffer offer = new JobOffer();
        StudentCv cv = new StudentCv();
        cv.setId(4L);
        cv.setStatus(cvStatus);
        when(jobOfferRepository.findById(2L)).thenReturn(Optional.of(offer));
        when(studentCvRepository.findById(4L)).thenReturn(Optional.of(cv));

        CvException exception = assertThrows(CvException.class,
                () -> service.applyToJobOffer(2L, CvMetaDataDto.builder().id(4L).build()));

        assertEquals("errors.noValidatedCv", exception.getMessage());
        assertTrue(offer.getAppliedCvs().isEmpty());
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }
}
