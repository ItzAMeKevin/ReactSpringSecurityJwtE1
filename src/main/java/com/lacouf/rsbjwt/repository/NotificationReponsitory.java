package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationReponsitory extends JpaRepository<Notification, Long> {
    List<Notification> findAllByStudent_IdOrderByCreatedAtDesc(Long studentId);
    Optional<Notification> findByIdAndStudent_Id(Long id, Long studentId);
}
