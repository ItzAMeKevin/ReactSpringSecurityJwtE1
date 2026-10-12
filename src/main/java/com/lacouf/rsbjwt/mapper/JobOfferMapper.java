package com.lacouf.rsbjwt.mapper;


import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = AdresseMapper.class)
public interface JobOfferMapper {

    @Mapping(target = "employer", ignore = true)
    JobOffer toEntity(JobOfferCreateDTO dto);


    @Mapping(target = "companyName", source = "employer.companyName")
    @Mapping(target = "location", source = "adresse")
    JobOfferDetailDTO toDto(JobOffer entity);

    List<JobOfferDetailDTO> toDtoList(List<JobOffer> entities);
}
