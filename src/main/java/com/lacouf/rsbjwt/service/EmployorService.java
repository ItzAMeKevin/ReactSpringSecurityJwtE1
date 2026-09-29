package com.lacouf.rsbjwt.service;


import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.service.dto.JobOfferDTO;
import org.springframework.stereotype.Service;

@Service
public class EmployorService {

    public JobOfferDTO addJobOffer(JobOfferCreateDTOreateDTO jobOfferDTO){
        JobOffer jobOfferEntity = JobOfferDTO.toEntity(jobOfferDTO);
        JobOffer offreSaved = JobOffreRepository.save(jobOfferEntity);
        return JobOfferDTO.toDto(offreSaved);
    }


}
