package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.InternshipSiteEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipSiteEvaluationRepository extends JpaRepository<InternshipSiteEvaluation, Long> {

    boolean existsByJobApplicationId(Long jobApplicationId);
}
