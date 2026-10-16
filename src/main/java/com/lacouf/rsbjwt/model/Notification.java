package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(nullable = true, length = 1000)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Status status = Status.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private Instant sentAt;

    @Column(nullable = true)
    private Integer retryCount = 0;

    @Column(nullable = true)
    private Instant lastRetryAt;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = true)
    private Manager manager;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_cv_id", nullable = true)
    private StudentCv studentCv;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = Status.PENDING;
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }
}

