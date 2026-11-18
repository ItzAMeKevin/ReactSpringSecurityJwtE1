package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Candidature {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "student_id", nullable = false)
        private Student student;



        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "job_offer_id", nullable = false)
        private JobOffer jobOffer;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Status status = Status.PENDING;

        @CreationTimestamp
        @Column(updatable = false)
        private Instant createdAt;

        @UpdateTimestamp
        private Instant updatedAt;


}
