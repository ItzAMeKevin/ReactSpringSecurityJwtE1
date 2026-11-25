package com.lacouf.rsbjwt.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor 
@AllArgsConstructor 
@Getter
@Setter
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private Status status;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @OneToOne(mappedBy = "contract")
    private Student student;

    @OneToOne(mappedBy = "contract")
    private Employer employer;

    @OneToOne(mappedBy = "contract")
    private Manager manager;

    @PrePersist
    void onCreate() {
        this.uploadedAt = Instant.now();
    }
}
