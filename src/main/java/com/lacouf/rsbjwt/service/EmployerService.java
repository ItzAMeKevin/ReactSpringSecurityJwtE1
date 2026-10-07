package com.lacouf.rsbjwt.service;


import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployerService {

    private final JobOfferRepository jobOfferRepository;
    private final EmployerRepository employerRepository;
    private final JobOfferMapper jobOfferMapper;


    public EmployerService(JobOfferRepository jobOfferRepository, EmployerRepository employerRepository, JobOfferMapper jobOfferMapper) {
        this.jobOfferRepository = jobOfferRepository;
        this.employerRepository = employerRepository;
        this.jobOfferMapper = jobOfferMapper;
    }

    public List<JobOfferDetailDTO> getJobOffres(String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        return jobOfferRepository.getJobOffersByEmployerId(employer.getId())
                .stream()
                .map(jobOfferMapper::toDto)
                .toList();
    }

    public JobOfferDetailDTO addJobOffer(JobOfferCreateDTO jobOfferDTO, String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        JobOffer jobOfferEntity = jobOfferMapper.toEntity(jobOfferDTO);
        jobOfferEntity.setEmployer(employer);
        JobOffer savedOffer = jobOfferRepository.save(jobOfferEntity);
        return jobOfferMapper.toDto(savedOffer);
    }

    public Employer findEmployerByEmail(String email) {
        return employerRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> new EmployerNotFoundException(email));
    }


}
