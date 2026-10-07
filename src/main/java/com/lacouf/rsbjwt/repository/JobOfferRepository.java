package com.lacouf.rsbjwt.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> getJobOffersByEmployerId(Long employerId);
    List<JobOffer> findAllByStatus(OfferStatus status);
    Optional<JobOffer> findByIdAndEmployerCredentialsEmail(Long id, String email);
}
