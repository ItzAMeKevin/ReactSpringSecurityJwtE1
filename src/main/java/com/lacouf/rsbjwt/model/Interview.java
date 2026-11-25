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

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = false, unique = true)
    private JobApplication jobApplication;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewStatus status = InterviewStatus.PENDING;

    @Column(length = 1000)
    private String studentMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewType type;

    private String videoLink;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "pay", column = @Column(name = "adresse_pays")),
            @AttributeOverride(name = "ville", column = @Column(name = "adresse_ville")),
            @AttributeOverride(name = "rue", column = @Column(name = "adresse_rue")),
            @AttributeOverride(name = "numeroCivic", column = @Column(name = "adresse_numero_civic")),
            @AttributeOverride(name = "codePostal", column = @Column(name = "adresse_code_postal"))
    })
    private Adresse adresse;

    @Column(length = 500)
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant scheduledAt;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewSlot> slots = new ArrayList<>();
}
