package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class EmployerNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    private boolean isRead = false;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employer_id")
    private Employer employer;

    public EmployerNotification(String title, String message, Employer employer) {
        this.title = title;
        this.message = message;
        this.employer = employer;
    }

}
