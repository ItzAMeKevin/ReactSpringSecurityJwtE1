package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
}
