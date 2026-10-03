package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.ManagerNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerNotificationRepository extends JpaRepository<ManagerNotification, Long> {
    List<ManagerNotification> findByManager_IdOrderByCreatedAtDesc(Long managerId);
    Optional<ManagerNotification> findByManager_IdOrderById(Long id, Long managerId);
}
