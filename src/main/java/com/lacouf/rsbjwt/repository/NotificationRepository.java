package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByStudent_IdOrderByCreatedAtDesc(Long studentId);
    Optional<Notification> findByIdAndStudent_Id(Long id, Long studentId);
    
    Optional<Notification> findByIdAndManager_Id(Long id, Long managerId);
    
    List<Notification> findAllByStatusAndLastRetryAtBeforeOrderByCreatedAtAsc(NotificationStatus status, Instant beforeTime);
    
    @Query("SELECT n FROM Notification n WHERE n.manager.id = :managerId " +
           "AND (n.type != 'CV_UPLOADED' OR (n.studentCv IS NOT NULL AND n.studentCv.status = 'PENDING')) " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findManagerNotificationsWithPendingCvs(@Param("managerId") Long managerId);
}
