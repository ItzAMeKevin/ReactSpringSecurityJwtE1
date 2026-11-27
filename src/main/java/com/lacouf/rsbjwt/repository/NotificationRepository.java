package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_Credentials_Email(String userEmail);

    Optional<Notification> findByIdAndUser_Credentials_Email(Long notificationId, String userEmail);

    boolean existsByUserAndNotificationTypeAndReferenceId(User user, NotificationType type, Long referenceId);
}
