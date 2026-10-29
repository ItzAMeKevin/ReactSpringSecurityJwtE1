package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "job_applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_offer_id", "student_cv_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_offer_id", nullable = false)
    private JobOffer jobOffer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_cv_id", nullable = false)
    private StudentCv studentCv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant appliedAt;

    @PrePersist
    void onCreate() {
        if (appliedAt == null) {
            appliedAt = Instant.now();
        }
        if (status == null) {
            status = Status.PENDING;
        }
    }
}
