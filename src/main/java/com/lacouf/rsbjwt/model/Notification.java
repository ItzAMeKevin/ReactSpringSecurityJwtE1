package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private NotificationType notificationType;

    @NotNull
    @Column(nullable = false)
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    private NotificationMessage message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.UNREAD;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private Instant creationDate = Instant.now();

    @PrePersist 
    public void prePersist() {
        if (this.creationDate == null) {
            this.creationDate = Instant.now();
        }
        if (this.status == null) {
            this.status = Status.UNREAD;
        }
    }
}

