package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CvMapper {
    @Mapping(target = "studentFirstName", source = "student.firstName")
    @Mapping(target = "studentLastName",  source = "student.lastName")
    @Mapping(target = "studentMatricule", source = "student.matricule")
    PendingCvDto toDto(StudentCv entity);
}

