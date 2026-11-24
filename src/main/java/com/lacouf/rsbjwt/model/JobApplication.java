package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

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
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant appliedAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public static final Set<Status> HIRED_STATUSES = EnumSet.of(
            Status.AWAITING_CONTRACT, Status.AWAITING_SIGNATURES, Status.CONTRACT_SIGNED, Status.WORKING, Status.COMPLETED);

    public static final Set<Status> SEAT_HOLDING_STATUSES = EnumSet.of(
            Status.HIRE_PENDING, Status.AWAITING_CONTRACT, Status.AWAITING_SIGNATURES, Status.CONTRACT_SIGNED, Status.WORKING, Status.COMPLETED);

    private static final Set<Status> OPEN_STATUSES = EnumSet.of(
            Status.PENDING, Status.PENDING_INTERVIEW, Status.INTERVIEW_SCHEDULED, Status.INTERVIEW_COMPLETED);

    public void offerHire() {
        transition(OPEN_STATUSES, Status.HIRE_PENDING, "errors.applicationNotOpen");
    }

    public void reject() {
        transition(OPEN_STATUSES, Status.DECLINED, "errors.applicationNotOpen");
    }

    public void acceptHire() {
        transition(EnumSet.of(Status.HIRE_PENDING), Status.AWAITING_CONTRACT, "errors.applicationNotHirePending");
    }

    public void declineHire() {
        transition(EnumSet.of(Status.HIRE_PENDING), Status.DECLINED, "errors.applicationNotHirePending");
    }

    private void transition(Set<Status> allowedFrom, Status target, String errorKey) {
        if (!allowedFrom.contains(status)) {
            throw new InvalidStateTransitionException(errorKey);
        }
        status = target;
    }
}
