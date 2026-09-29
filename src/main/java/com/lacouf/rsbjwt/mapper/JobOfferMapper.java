package com.lacouf.rsbjwt.mapper;


import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOffreDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface JobOfferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "employer", ignore = true)
    JobOffer toEntity(JobOfferCreateDTO dto);

    @Mapping(target = "employerId", source = "employer.id")
    @Mapping(target = "employerName", source = "employer.companyName")
    JobOffreDetailDTO toDto(JobOffer entity);
}