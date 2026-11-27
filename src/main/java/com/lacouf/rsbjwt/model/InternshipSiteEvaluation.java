package com.lacouf.rsbjwt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InternshipSiteEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = false, unique = true)
    private JobApplication jobApplication;

    // Company identification
    private String companyName;
    private String contactPerson;
    private String address;
    private String phone;
    private String city;
    private String fax;
    private String postalCode;

    // Intern identification
    private String internName;
    private String internshipDate;
    private Integer internshipNumber;

    // Evaluation criteria (Likert: TA / PA / PD / TD / NA)
    @Column(length = 3) private String tasks;
    @Column(length = 3) private String onboarding;
    @Column(length = 3) private String supervision;
    private Integer supervisionHoursMonth1;
    private Integer supervisionHoursMonth2;
    private Integer supervisionHoursMonth3;
    @Column(length = 3) private String salary;
    private String hourlyRate;
    @Column(length = 3) private String communication;
    @Column(length = 3) private String equipment;
    @Column(length = 3) private String workload;
    @Column(length = 3) private String healthSafety;
    @Column(length = 3) private String workAtmosphere;
    @Column(length = 3) private String publicTransit;

    // Comments
    @Column(length = 2000)
    private String comments;

    // General observations
    private Integer preferredInternshipNumber;
    @Column(length = 5) private String maxInterns;
    private Boolean welcomeSameIntern;
    private Boolean hasVariableShifts;
    private String shift1Start;
    private String shift1End;
    private String shift2Start;
    private String shift2End;
    private String shift3Start;
    private String shift3End;

    // Signature
    private String teacherSignature;
    private String signatureDate;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant submittedAt;
}
