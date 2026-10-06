package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByStudent_IdOrderByCreatedAtDesc(Long studentId);
    Optional<Notification> findByIdAndStudent_Id(Long id, Long studentId);
    
    List<Notification> findAllByManager_IdOrderByCreatedAtDesc(Long managerId);
    Optional<Notification> findByIdAndManager_Id(Long id, Long managerId);
    
    List<Notification> findAllByStatusOrderByCreatedAtAsc(NotificationStatus status);
    List<Notification> findAllByStatusAndLastRetryAtBeforeOrderByCreatedAtAsc(NotificationStatus status, Instant beforeTime);
}
