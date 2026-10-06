package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.service.dto.StudentCreateDto;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = AdresseMapper.class, builder = @Builder(disableBuilder = true))
public interface StudentMapper {

    @Mapping(target = "lastname", source = "lastName")
    @Mapping(target = "adresseDTO", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "employerId", ignore = true)
    StudentCreateDto toDto(Student student);

    @Mapping(target = "credentials", source = "dto", qualifiedByName = "buildCredentials")
    @Mapping(target = "lastName", source = "lastname")
    Student toEntity(UserCreateDTO dto);

    @Named("buildCredentials")
    default Credentials buildCredentials(UserCreateDTO dto) {
        return Credentials.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.STUDENT)
                .build();
    }
}