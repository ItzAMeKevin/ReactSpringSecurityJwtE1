
package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.service.dto.CvMetaDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CvMetaDataMapper {

    @Mapping(target = "erreur", ignore = true)
    CvMetaDataDto toDto(StudentCv entity);

    @Mapping(target = "erreur", ignore = true)
    List<CvMetaDataDto> toDtoList(List<StudentCv> entities);
}