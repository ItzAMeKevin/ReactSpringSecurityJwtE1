package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.service.dto.EmployerCreateDto;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = AdresseMapper.class, builder = @Builder(disableBuilder = true))
public interface EmployerMapper {

    @Mapping(target = "lastname", source = "lastName")
    @Mapping(target = "adresseDTO", source = "adresse")
    @Mapping(target = "matricule", ignore = true)
    @Mapping(target = "programe", ignore = true)
    EmployerCreateDto toDto(Employer employer);

    @Mapping(target = "jobOffers", ignore = true)
    @Mapping(target = "credentials", source = "dto", qualifiedByName = "buildCredentials")
    @Mapping(target = "lastName", source = "lastname")
    @Mapping(target = "adresse", source = "adresseDTO")
    Employer toEntity(UserCreateDTO dto);

    @Named("buildCredentials")
    default Credentials buildCredentials(UserCreateDTO dto) {
        return Credentials.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.EMPLOYER)
                .build();
    }
}