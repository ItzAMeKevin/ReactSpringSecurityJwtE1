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
    private String companyName;
    private String offerTitle;
    private String program;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    public ManagerNotification(String title, String message, Long offerId, Manager manager) {
        this(title, message, offerId, manager, null, null, null);
    }

    public ManagerNotification(
            String title,
            String message,
            Long offerId,
            Manager manager,
            String companyName,
            String offerTitle,
            String program
    ) {
        this.title = title;
        this.message = message;
        this.offerId = offerId;
        this.manager = manager;
        this.companyName = companyName;
        this.offerTitle = offerTitle;
        this.program = program;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}
