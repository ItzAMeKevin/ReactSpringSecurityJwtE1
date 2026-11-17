
package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CvMetaDataMapper {

    CvMetaDataDto toDto(StudentCv entity);

    List<CvMetaDataDto> toDtoList(List<StudentCv> entities);
}