package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    @Query("SELECT n FROM Notification n WHERE n.user.credentials.email = :email")
    Optional<List<Notification>> findByUserEmail(@Param("email") String email);

    @Query("SELECT n FROM Notification n WHERE n.id = :id AND n.user.credentials.email = :email")
    Optional<Notification> findByIdAndUserEmail(@Param("id") Long id, @Param("email") String email);
}
