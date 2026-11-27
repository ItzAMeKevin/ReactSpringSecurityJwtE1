package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternEvalReminderSchedulerTest {

    @Mock private JobApplicationRepository jobApplicationRepository;
    @Mock private NotificationRepository notificationRepository;
    @InjectMocks private InternEvalReminderScheduler scheduler;

    // startingDate + durationInWeeks*7 = endDate
    // makeApp(today - 2, 1) → endDate = today + 5  (within 7-day window)
    // makeApp(today + 1, 1) → endDate = today + 8  (outside window)
    // makeApp(today,     1) → endDate = today + 7  (exactly on boundary)
    private JobApplication makeApp(LocalDate startingDate, int durationInWeeks) {
        Employer employer = new Employer();

        JobOffer offer = new JobOffer();
        offer.setStartingDate(startingDate);
        offer.setDurationInWeeks(durationInWeeks);
        offer.setEmployer(employer);

        Student student = new Student();
        student.setFirstName("Alice");
        student.setLastName("Smith");

        JobApplication app = new JobApplication();
        app.setId(1L);
        app.setJobOffer(offer);
        app.setStudent(student);

        return app;
    }

    @Test
    void sendsReminderWhenInternshipEndingWithinSevenDays() {
        JobApplication app = makeApp(LocalDate.now().minusDays(2), 1); // ends today + 5

        when(jobApplicationRepository.findByStatusWithEmployer(Status.WORKING)).thenReturn(List.of(app));
        when(notificationRepository.existsByUserAndNotificationTypeAndReferenceId(
                any(), eq(NotificationType.EVALUATION), anyLong())).thenReturn(false);

        scheduler.sendEvalReminders();

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void sendsReminderWhenInternshipEndsExactlyOnWarningDate() {
        JobApplication app = makeApp(LocalDate.now(), 1); // ends today + 7 (boundary)

        when(jobApplicationRepository.findByStatusWithEmployer(Status.WORKING)).thenReturn(List.of(app));
        when(notificationRepository.existsByUserAndNotificationTypeAndReferenceId(
                any(), eq(NotificationType.EVALUATION), anyLong())).thenReturn(false);

        scheduler.sendEvalReminders();

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void doesNotSendReminderWhenInternshipEndsAfterWarningPeriod() {
        JobApplication app = makeApp(LocalDate.now().plusDays(1), 1); // ends today + 8

        when(jobApplicationRepository.findByStatusWithEmployer(Status.WORKING)).thenReturn(List.of(app));

        scheduler.sendEvalReminders();

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void doesNotSendDuplicateReminderWhenAlreadyNotified() {
        JobApplication app = makeApp(LocalDate.now().minusDays(2), 1); // ends today + 5

        when(jobApplicationRepository.findByStatusWithEmployer(Status.WORKING)).thenReturn(List.of(app));
        when(notificationRepository.existsByUserAndNotificationTypeAndReferenceId(
                any(), eq(NotificationType.EVALUATION), anyLong())).thenReturn(true);

        scheduler.sendEvalReminders();

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void doesNotSendReminderWhenStartingDateIsNull() {
        JobApplication app = makeApp(null, 1);

        when(jobApplicationRepository.findByStatusWithEmployer(Status.WORKING)).thenReturn(List.of(app));

        scheduler.sendEvalReminders();

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void doesNothingWhenNoWorkingApplications() {
        when(jobApplicationRepository.findByStatusWithEmployer(Status.WORKING)).thenReturn(List.of());

        scheduler.sendEvalReminders();

        verify(notificationRepository, never()).save(any());
    }
}
