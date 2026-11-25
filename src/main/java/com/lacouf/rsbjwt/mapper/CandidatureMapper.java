package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidatureMapper {

    @Mapping(target = "createdAt", source = "appliedAt")
    @Mapping(target = "student",   source = "studentCv.student")
    @Mapping(target = "jobOffer",  source = "jobOffer")
    CandidatureDto toDto(JobApplication application);

    @Mapping(target = "email", source = "email")   // ⚠️ ou "credentials.email"
    CandidatureDto.StudentInfoDto toStudentInfo(Student student);

    @Mapping(target = "companyName", source = "employer.companyName") // ⚠️ getter réel
    CandidatureDto.JobOfferInfoDto toJobOfferInfo(JobOffer jobOffer);
}