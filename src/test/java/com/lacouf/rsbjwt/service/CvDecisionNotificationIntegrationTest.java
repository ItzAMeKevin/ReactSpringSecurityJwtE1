package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class CvDecisionNotificationIntegrationTest {

    private static final String BLOCK_ACCEPTED_NOTIFICATIONS =
            "ALTER TABLE notifications ADD CONSTRAINT test_block_cv_accepted "
                    + "CHECK (type <> 'CV_ACCEPTED') NOT VALID";

    @Autowired
    private GestionnaireService gestionnaireService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentCvRepository studentCvRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ThreadPoolTaskExecutor applicationTaskExecutor;

    @AfterEach
    void removeBlockingConstraint() {
        jdbcTemplate.execute("ALTER TABLE notifications DROP CONSTRAINT IF EXISTS test_block_cv_accepted");
    }

    @Test
    void acceptCv_notificationFails_cvStaysAccepted() {
        // ARRANGE
        StudentCv cv = savePendingCv();
        jdbcTemplate.execute(BLOCK_ACCEPTED_NOTIFICATIONS);

        // ACT
        assertDoesNotThrow(() -> gestionnaireService.acceptCv(cv.getId()));

        // ASSERT
        Status statusInDatabase = studentCvRepository.findById(cv.getId()).orElseThrow().getStatus();
        assertEquals(Status.ACCEPTED, statusInDatabase);
        waitForAsyncNotifications();
        assertEquals(0, countNotifications(cv.getId(), "CV_ACCEPTED"));
    }

    @Test
    void acceptCv_success_notifiesStudentAfterCommit() {
        // ARRANGE
        StudentCv cv = savePendingCv();

        // ACT
        gestionnaireService.acceptCv(cv.getId());
        waitForAsyncNotifications();

        // ASSERT
        assertEquals(1, countNotifications(cv.getId(), "CV_ACCEPTED"));
    }

    private void waitForAsyncNotifications() {
        await().atMost(Duration.ofSeconds(5)).until(() ->
                applicationTaskExecutor.getActiveCount() == 0
                        && applicationTaskExecutor.getThreadPoolExecutor().getQueue().isEmpty());
    }

    private int countNotifications(Long cvId, String type) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notifications WHERE student_cv_id = ? AND type = ?",
                Integer.class, cvId, type);
    }

    private StudentCv savePendingCv() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        Student student = studentRepository.save(Student.builder()
                .firstName("Test")
                .lastName("Decision")
                .email("decision-" + unique + "@test.com")
                .password("password")
                .matricule("T" + unique)
                .programe(Programe.TECHNIQUES_INFORMATIQUE)
                .build());

        StudentCv cv = new StudentCv();
        cv.setFileName("cv-test.pdf");
        cv.setContentType("application/pdf");
        cv.setContent(new byte[] {37, 80, 68, 70, 45});
        cv.setSize(5);
        cv.setStatus(Status.PENDING);
        cv.setStudent(student);
        return studentCvRepository.save(cv);
    }
}
