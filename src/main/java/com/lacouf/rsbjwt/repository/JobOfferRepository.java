package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> findAllByStatus(Status status);
    List<JobOffer> getJobOffersByEmployerId(Long employerId);
    List<JobOffer> findAllByStatusAndPrograme(Status status, Programe programe);
    @Query("""
        SELECT jo FROM JobOffer jo
        LEFT JOIN FETCH jo.appliedCvs cv
        LEFT JOIN FETCH cv.student
        WHERE jo.id = :id AND jo.employer.credentials.email = :email
        """)
    Optional<JobOffer> findByIdAndEmployerCredentialsEmail(@Param("id") Long id, @Param("email") String email);

    boolean existsByEmployer_Credentials_EmailAndAppliedCvs_Id(String email, Long cvId);

    @Query("""
        SELECT DISTINCT jo
        FROM JobOffer jo
        JOIN jo.appliedCvs cv
        JOIN cv.student student
        WHERE jo.id = :id
          AND student.credentials.email = :studentEmail
        """)
    Optional<JobOffer> findByIdAndStudentCredentialsEmail(
            @Param("id") Long id,
            @Param("studentEmail") String studentEmail);
}
