package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "student_cvs")
@Getter
@Setter
@NoArgsConstructor
public class StudentCv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Pas de @Lob : sur PostgreSQL, un byte[] devient une colonne bytea
    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CvStatus status;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @PrePersist
    void onCreate() {
        this.uploadedAt = Instant.now();
        if (this.status == null) {
            this.status = CvStatus.PENDING;
        }
    }
}
