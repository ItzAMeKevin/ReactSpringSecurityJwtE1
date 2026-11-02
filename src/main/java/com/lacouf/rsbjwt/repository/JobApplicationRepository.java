package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    @Query("""
            SELECT ja FROM JobApplication ja
            JOIN FETCH ja.studentCv cv
            JOIN FETCH cv.student
            WHERE ja.jobOffer.id = :offerId
            ORDER BY ja.appliedAt DESC
            """)
    List<JobApplication> findByOfferIdWithStudent(@Param("offerId") Long offerId);

    @Query("""
            SELECT ja FROM JobApplication ja
            JOIN FETCH ja.studentCv cv
            JOIN FETCH cv.student
            WHERE ja.jobOffer.id = :offerId AND cv.id = :cvId
            """)
    Optional<JobApplication> findByOfferIdAndCvIdWithStudent(@Param("offerId") Long offerId, @Param("cvId") Long cvId);

    boolean existsByStudentCv_IdAndJobOffer_Employer_Credentials_Email(Long cvId, String email);

    @Query("""
        SELECT ja FROM JobApplication ja
        JOIN FETCH ja.jobOffer
        WHERE ja.studentCv.student.id = :studentId
        ORDER BY ja.appliedAt DESC
        """)
    List<JobApplication> findByStudentIdWithOffer(@Param("studentId") Long studentId);
}
