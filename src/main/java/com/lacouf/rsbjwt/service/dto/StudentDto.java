package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
public class StudentDto extends UserDTO {

    @Builder
    public StudentDto(Long id, String firstName, String lastname, String email, String password, Role role, String matricule) {
        super(id, firstName, lastname, email, password, role, matricule);
    }

    public StudentDto() {}

    public static StudentDto toStudentDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastname(student.getLastName())
                .email(student.getEmail())
                .password(student.getPassword())
                .role(student.getRole())
                .build();
    }

    public static StudentDto empty() {
        return new StudentDto();
    }
}
