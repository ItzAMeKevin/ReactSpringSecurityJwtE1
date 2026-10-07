package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.service.dto.ManagerCreateDto;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = AdresseMapper.class, builder = @Builder(disableBuilder = true))
public interface ManagerMapper {

    @Mapping(target = "lastname", source = "lastName")
    @Mapping(target = "adresseDTO", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "programe", ignore = true)
    ManagerCreateDto toDto(Manager manager);

    @Mapping(target = "credentials", source = "dto", qualifiedByName = "buildCredentials")
    @Mapping(target = "lastName", source = "lastname")
    Manager toEntity(UserCreateDTO dto);

    @Named("buildCredentials")
    default Credentials buildCredentials(UserCreateDTO dto) {
        return Credentials.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.MANAGER)
                .build();
    }
}