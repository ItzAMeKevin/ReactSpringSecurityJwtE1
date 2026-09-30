package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
public class StudentCreateDto extends UserCreateDTO {

    @Builder
    public StudentCreateDto(Long id, String firstName, String lastName, String email, String password, Role role, String matricule, Programe programe) {
        super(id, firstName, lastName, email, password, role, matricule,
                null, null, null, null, null, null,programe);

    }

    public StudentCreateDto() {
    }

    public static StudentCreateDto toStudentDto(Student student) {
        return StudentCreateDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .password(student.getPassword())
                .role(student.getRole())
                .programe(student.getPrograme())
                .build();
    }

    public static StudentCreateDto empty() {
        return new StudentCreateDto();
    }
}
