package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(source = "studentCv.id", target = "cvId")
    @Mapping(source = "id", target = "candidatureId")
    @Mapping(source = "studentCv.student.firstName", target = "firstName")
    @Mapping(source = "studentCv.student.lastName", target = "lastName")
    @Mapping(source = "studentCv.student.email", target = "email")
    CandidateDto toCandidateDto(JobApplication application);


    @Mapping(source = "cv.id",                target = "cvId")
    @Mapping(target = "candidatureId",        ignore = true)
    @Mapping(source = "cv.student.firstName", target = "firstName")
    @Mapping(source = "cv.student.lastName",  target = "lastName")
    @Mapping(source = "cv.student.email",     target = "email")
    CandidateDto toCandidateDto(StudentCv cv, Instant appliedAt, Status status);
}
