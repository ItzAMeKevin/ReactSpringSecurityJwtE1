package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.service.dto.EmployerDetailDto;
import com.lacouf.rsbjwt.service.dto.JobApplicationSuiviDto;
import com.lacouf.rsbjwt.service.dto.StudentDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JobOfferMapper.class, AdresseMapper.class})
public interface JobApplicationSuiviMapper {

    @Mapping(source = "studentCv.student", target = "student")
    @Mapping(source = "jobOffer.employer", target = "employer")
    JobApplicationSuiviDto toSuiviDto(JobApplication application);

    List<JobApplicationSuiviDto> toDtoList(List<JobApplication> applications);

    StudentDetailsDto toStudentDetailsDto(Student student);

    EmployerDetailDto toEmployerDetailDto(Employer employer);
}
