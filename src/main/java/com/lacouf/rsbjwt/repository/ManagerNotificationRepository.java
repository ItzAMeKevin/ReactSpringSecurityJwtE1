package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.ManagerNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerNotificationRepository extends JpaRepository<ManagerNotification, Long> {
    List<ManagerNotification> findAllByManager_IdOrderByCreatedAtDesc(Long managerId);
    Optional<ManagerNotification> findByIdAndManager_Id(Long id, Long managerId);
}
