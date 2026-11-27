package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InternEvalReminderScheduler {

    static final int DAYS_BEFORE_END = 7;

    private final JobApplicationRepository jobApplicationRepository;
    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendEvalReminders() {
        LocalDate warningDate = LocalDate.now().plusDays(DAYS_BEFORE_END);

        jobApplicationRepository.findByStatusWithEmployer(Status.WORKING).stream()
                .filter(app -> isEndingBy(app.getJobOffer(), warningDate))
                .filter(app -> !notificationAlreadySent(app))
                .forEach(this::sendReminder);
    }

    private boolean isEndingBy(JobOffer offer, LocalDate warningDate) {
        if (offer.getStartingDate() == null) return false;
        LocalDate endDate = offer.getStartingDate().plusWeeks(offer.getDurationInWeeks());
        return !endDate.isAfter(warningDate);
    }

    private boolean notificationAlreadySent(JobApplication app) {
        return notificationRepository.existsByUserAndNotificationTypeAndReferenceId(
                app.getJobOffer().getEmployer(), NotificationType.EVALUATION, app.getId());
    }

    private void sendReminder(JobApplication app) {
        Student intern = app.getStudent();
        LocalDate endDate = app.getJobOffer().getStartingDate()
                .plusWeeks(app.getJobOffer().getDurationInWeeks());
        String message = "notification.types.EVAL_REQUIRED.message"
                + "|" + intern.getFirstName() + " " + intern.getLastName()
                + "|" + endDate;
        notificationRepository.save(Notification.builder()
                .user(app.getJobOffer().getEmployer())
                .notificationType(NotificationType.EVALUATION)
                .referenceId(app.getId())
                .message(message)
                .build());
    }
}
