package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.InternshipSiteEvaluation;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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
    boolean existsByStudentCv_IdAndJobOffer_Id(Long cvId, Long offerId);

    long countByJobOffer_IdAndStatusIn(Long jobOfferId, Collection<Status> statuses);

    @Query("""
        SELECT ja.jobOffer.id, COUNT(ja)
        FROM JobApplication ja
        WHERE ja.jobOffer.id IN :offerIds AND ja.status IN :statuses
        GROUP BY ja.jobOffer.id
        """)
    List<Object[]> countByOfferIdsAndStatusIn(@Param("offerIds") List<Long> offerIds, @Param("statuses") Collection<Status> statuses);

    @Query("""
        SELECT ja FROM JobApplication ja
        JOIN FETCH ja.jobOffer
        WHERE ja.studentCv.student.id = :studentId
        ORDER BY ja.appliedAt DESC
        """)
    List<JobApplication> findByStudentIdWithOffer(@Param("studentId") Long studentId);

    @Query("""
        SELECT ja FROM JobApplication ja
        JOIN FETCH ja.jobOffer jo
        JOIN FETCH jo.employer
        WHERE ja.id = :id
        """)
    Optional<JobApplication> findByIdWithEmployer(@Param("id") Long id);

    @Query("""
        SELECT ja FROM JobApplication ja
        JOIN FETCH ja.studentCv cv
        JOIN FETCH cv.student s
        JOIN FETCH ja.jobOffer jo
        JOIN FETCH jo.employer e
        ORDER BY ja.appliedAt DESC
        """)
    List<JobApplication> findAllWithStudentAndOffer();

    @Query("""
        SELECT ja FROM JobApplication ja
        JOIN FETCH ja.studentCv cv
        JOIN FETCH cv.student s
        JOIN FETCH ja.jobOffer jo
        JOIN FETCH jo.employer e
        WHERE e.credentials.email = :email AND ja.status = :status
        ORDER BY ja.appliedAt DESC
        """)
    List<JobApplication> findByEmployerEmailAndStatus(@Param("email") String email, @Param("status") Status status);

    @Query("""
        SELECT ja FROM JobApplication ja
        JOIN FETCH ja.studentCv cv
        JOIN FETCH cv.student s
        JOIN FETCH ja.jobOffer jo
        JOIN FETCH jo.employer
        WHERE s.credentials.email = :email AND ja.status = :status
        ORDER BY ja.appliedAt DESC
        """)
    List<JobApplication> findByStudentEmailAndStatus(@Param("email") String email, @Param("status") Status status);

    @Query("""
        SELECT ja FROM JobApplication ja
        JOIN FETCH ja.studentCv cv
        JOIN FETCH cv.student s
        JOIN FETCH ja.jobOffer jo
        JOIN FETCH jo.employer
        WHERE ja.id = :id AND s.credentials.email = :email
        """)
    Optional<JobApplication> findByIdAndStudentEmail(@Param("id") Long id, @Param("email") String email);

    @Query("""
        SELECT DISTINCT ja FROM JobApplication ja
        JOIN FETCH ja.student s
        JOIN FETCH s.credentials
        JOIN FETCH ja.jobOffer jo
        JOIN FETCH jo.employer
        WHERE ja.status = :status
        AND NOT EXISTS (SELECT e FROM InternshipSiteEvaluation e WHERE e.jobApplication = ja)
        ORDER BY ja.appliedAt DESC
        """)
    List<JobApplication> findByStatusWithoutEvaluation(@Param("status") Status status);

    @Modifying
    @Query("UPDATE JobApplication ja SET ja.status = :status WHERE ja.id = :applicationId")
    int updateStatus(@Param("applicationId") Long applicationId, @Param("status") Status status);
}
