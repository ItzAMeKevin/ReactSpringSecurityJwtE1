package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> findAllByStatus(Status status);
    List<JobOffer> getJobOffersByEmployerId(Long employerId);
}
