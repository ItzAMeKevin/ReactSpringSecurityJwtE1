package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull
    private NotificationType notificationType;

    @NotNull
    private Long referenceId;

    @NotBlank
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.UNREAD;

    @NotNull
    private Instant creationDate;

    @PrePersist 
    public void prePersist() {
        this.creationDate = Instant.now();
        this.status = Status.UNREAD;
    }
}

