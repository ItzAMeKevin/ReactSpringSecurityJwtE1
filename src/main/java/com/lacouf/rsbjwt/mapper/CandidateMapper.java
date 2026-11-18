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


    @Mapping(target = "cvId",          source = "studentCv.id")
    @Mapping(target = "candidatureId", source = "id")
    @Mapping(target = "firstName",     source = "studentCv.student.firstName")
    @Mapping(target = "lastName",      source = "studentCv.student.lastName")
    @Mapping(target = "email",         source = "studentCv.student.email")
    @Mapping(target = "status",        source = "status")
    @Mapping(target = "appliedAt",     source = "appliedAt")
    CandidateDto toDto(JobApplication application);


    @Mapping(target = "cvId",          source = "cv.id")
    @Mapping(target = "candidatureId", source = "candidatureId")
    @Mapping(target = "firstName",     source = "cv.student.firstName")
    @Mapping(target = "lastName",      source = "cv.student.lastName")
    @Mapping(target = "email",         source = "cv.student.email")
    @Mapping(target = "status",        source = "status")
    @Mapping(target = "appliedAt",     source = "appliedAt")
    CandidateDto toDto(StudentCv cv, Long candidatureId, Instant appliedAt, Status status);



}