package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> getJobOffersByEmployerId(Long employerId);
}
