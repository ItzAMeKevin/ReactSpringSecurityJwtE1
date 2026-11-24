package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    @Query("""
            SELECT DISTINCT i FROM Interview i
            JOIN FETCH i.jobApplication ja
            JOIN FETCH ja.jobOffer jo
            JOIN FETCH ja.studentCv cv
            JOIN FETCH cv.student
            LEFT JOIN FETCH i.slots
            WHERE jo.employer.credentials.email = :email
            ORDER BY i.scheduledAt DESC
            """)
    List<Interview> findAllByEmployerEmail(@Param("email") String email);

    @Query("""
            SELECT DISTINCT i FROM Interview i
            JOIN FETCH i.jobApplication ja
            JOIN FETCH ja.jobOffer jo
            JOIN FETCH jo.employer
            JOIN FETCH ja.studentCv cv
            JOIN FETCH cv.student
            LEFT JOIN FETCH i.slots
            WHERE cv.student.credentials.email = :email
            ORDER BY i.scheduledAt DESC
            """)
    List<Interview> findAllByStudentEmail(@Param("email") String email);

    @Query("""
            SELECT DISTINCT i FROM Interview i
            JOIN FETCH i.jobApplication ja
            JOIN FETCH ja.jobOffer jo
            JOIN FETCH jo.employer
            JOIN FETCH ja.studentCv cv
            JOIN FETCH cv.student
            LEFT JOIN FETCH i.slots
            WHERE i.id = :id AND cv.student.credentials.email = :email
            """)
    Optional<Interview> findByIdAndStudentEmail(@Param("id") Long id, @Param("email") String email);

    boolean existsByJobApplicationId(Long jobApplicationId);
}
