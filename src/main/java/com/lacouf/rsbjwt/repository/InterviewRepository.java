package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByJobApplication_Student_Credentials_Email(String email);

    Optional<Interview> findByIdAndJobApplication_Student_Credentials_Email(Long id, String email);

    boolean existsByJobApplicationId(Long jobApplicationId);
}
