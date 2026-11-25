package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Contract;
import com.lacouf.rsbjwt.service.dto.ContractDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ContractMapper {

    @Mapping(target = "erreur", ignore = true)
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "employerId", source = "employer.id")
    @Mapping(target = "managerId", source = "manager.id")
    ContractDto toDto(Contract contract);

    @Mapping(target = "erreur", ignore = true)
    List<ContractDto> toDtoList(List<Contract> contracts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "signedByStudent", ignore = true)
    @Mapping(target = "signedByEmployer", ignore = true)
    @Mapping(target = "signedByManager", ignore = true)
    @Mapping(target = "signature", ignore = true)
    Contract toEntity(ContractDto contractDto);
}
