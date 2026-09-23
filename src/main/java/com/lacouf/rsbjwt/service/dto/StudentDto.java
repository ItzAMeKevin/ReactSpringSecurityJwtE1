package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;
import lombok.Data;

@Data
public class StudentDto extends UserDTO {

    @Builder
    public StudentDto(Long id, String firstName, String lastName, String email, String password, Role role, String matricule, Programe programe) {
        super(id, firstName, lastName, email, password, role, matricule,
                null, null, null, null, null, null,programe);

    }

    public StudentDto() {
    }

    public static StudentDto toStudentDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .password(student.getPassword())
                .role(student.getRole())
                .programe(student.getPrograme())
                .build();
    }

    public static StudentDto empty() {
        return new StudentDto();
    }
}
