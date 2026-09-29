package com.lacouf.rsbjwt.service;


import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDTO;
import com.lacouf.rsbjwt.service.dto.JobOffreDetailDTO;
import org.springframework.stereotype.Service;
import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployorService {

    private final JobOfferRepository jobOfferRepository;
    private final EmployerRepository employerRepository;
    private final JobOfferMapper jobOfferMapper;

    public EmployorService(JobOfferRepository jobOfferRepository, EmployerRepository employerRepository, JobOfferMapper jobOfferMapper) {
        this.jobOfferRepository = jobOfferRepository;
        this.employerRepository = employerRepository;
        this.jobOfferMapper = jobOfferMapper;
    }

    public JobOffreDetailDTO addJobOffer(JobOfferCreateDTO jobOfferDTO){

        Employer employer = findEmployerById(jobOfferDTO.employerId());
        JobOffer jobOfferEntity = jobOfferMapper.toEntity(jobOfferDTO);
        jobOfferEntity.setEmployer(employer);
        JobOffer savedOffer = jobOfferRepository.save(jobOfferEntity);

        return jobOfferMapper.toDto(savedOffer);
    }

    public Employer findEmployerById (Long id){
        return employerRepository.findById(id).orElseThrow(()-> new EmployerNotFoundException(id));
    }


}
