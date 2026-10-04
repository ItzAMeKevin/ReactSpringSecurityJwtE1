package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class ManagerNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    private boolean isRead = false;

    @CreationTimestamp
    private Instant createdAt;
    private Long offerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    public ManagerNotification(String title, String message, Long offerId, Manager manager) {
        this.title = title;
        this.message = message;
        this.offerId = offerId;
        this.manager = manager;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}
