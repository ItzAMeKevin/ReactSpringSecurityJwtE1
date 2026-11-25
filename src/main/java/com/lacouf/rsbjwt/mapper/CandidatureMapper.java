package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidatureMapper {

    @Mapping(source = "studentCv.student.firstName", target = "student.firstName")
    @Mapping(source = "studentCv.student.lastName", target = "student.lastName")
    @Mapping(source = "studentCv.student.matricule", target = "student.matricule")
    @Mapping(source = "studentCv.student.credentials.email", target = "student.email")
    @Mapping(source = "jobOffer.title", target = "jobOffer.title")
    @Mapping(source = "jobOffer.employer.companyName", target = "jobOffer.companyName")
    @Mapping(source = "jobOffer.durationInWeeks", target = "jobOffer.durationInWeeks")
    CandidatureDto toCandidatureDto(JobApplication application);

    List<CandidatureDto> toDtoList(List<JobApplication> applications);

}
