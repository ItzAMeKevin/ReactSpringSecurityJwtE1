package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewType type;

    private String videoLink;

    private String address;

    @Column(length = 500)
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant scheduledAt;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewSlot> slots = new ArrayList<>();
}
