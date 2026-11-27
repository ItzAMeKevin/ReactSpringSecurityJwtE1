package com.lacouf.rsbjwt.service;


import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.mapper.NotificationMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class SystemeService{

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ManagerRepository managerRepository;
    private final EmployerRepository employerRepository;
    private final StudentRepository studentRepository;
    private final StudentCvRepository cvRepository;
    private final JobOfferRepository jobOfferRepository;
    private final UserAppRepository userAppRepository;


    private Notification buildNotification(
            User user, NotificationType type, Long referenceId, String messageKey) {
        return Notification.builder()
                .user(user)
                .notificationType(type)
                .referenceId(referenceId)
                .message(messageKey)
                .build();
    }

    public List<NotificationDto> getNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        return notificationMapper.toDtoList(notificationRepository.findByUser_Credentials_Email(userEmail));
    }

    public void notifyUsers(List<? extends User> users,
                            NotificationType type, Long referenceId, String messageKey) {
        List<Notification> notifications = users.stream()
                .map(user -> buildNotification(user, type, referenceId, messageKey))
                .toList();
        saveNotifications(notifications);
    }


    private void saveNotifications(List<Notification> notifications) {
        if (notifications.isEmpty()) return;
        notificationRepository.saveAll(notifications);
    }



    public void notifyManagers(NotificationType type, Long refId, String msg) {
        notifyUsers(managerRepository.findAll(), type, refId, msg);
    }


    public void notifyAllEmployers(NotificationType type, Long refId, String msg) {
        notifyUsers(employerRepository.findAll(), type, refId, msg);
    }

    public void notifyStudentsOfOffer( Long refId, String msg) {
        Programe programe = getProgrameForOffer( refId);
        if (programe == null) return;
        notifyUsers(studentRepository.findByPrograme(programe), NotificationType.JOB_OFFER, refId, msg);
    }

    public void notifyAllStudents(NotificationType type, Long refId, String msg) {
        notifyUsers(studentRepository.findAll(), type, refId, msg);
    }

    private Programe getProgrameForOffer( Long refId) {
            return jobOfferRepository.findById(refId)
                    .map(JobOffer::getPrograme)
                    .orElse(null);
    }



    public NotificationDto updateNotificationStatus(Authentication authentication, Long notificationId) throws NotificationNotFound {
        String userEmail = authentication.getName();
        return notificationRepository.findByIdAndUser_Credentials_Email(notificationId, userEmail)
                .map(notification -> {
                    notification.setStatus(Status.READ);
                    return notificationMapper.toDto(notificationRepository.save(notification));
                })
                .orElseThrow(NotificationNotFound::new);
    }


    public void saveSignature(Authentication authentication, byte[] imageBytes, String contentType) {
        String email = authentication.getName();
        User user = userAppRepository.findUserAppByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        Signature signature = new Signature(
                imageBytes
        );
        user.setSignature(signature);
        userAppRepository.save(user);
    }

    public byte[] getSignatureImage(Authentication authentication) {
        String email = authentication.getName();
        User user = userAppRepository.findUserAppByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        return user.getSignature() != null ? user.getSignature().getImageData() : null;
    }
}

