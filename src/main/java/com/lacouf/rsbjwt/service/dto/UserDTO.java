package com.lacouf.rsbjwt.service.dto;


import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.User;
import com.lacouf.rsbjwt.model.auth.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor 
@NoArgsConstructor
@Setter
@Getter 
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastname;
    private String email;
    private String password;
    private Role role;
    private String matricule;
    private String companyName;
    private String address;
    private String postalCode;
    private String city;
    private String phoneNumber;
    private String employerId;

    public User toEntity(UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case MANAGER -> Manager.builder()
                    .id(userDTO.getId())
                    .firstName(userDTO.getFirstName())
                    .password(userDTO.getPassword())
                    .matricule(userDTO.getMatricule())
                    .lastName(userDTO.getLastname())
                    .email(userDTO.getEmail())
                    .password(userDTO.getPassword())
                    .build();
            case EMPLOYER -> Employer.builder()
                    .id(userDTO.getId())
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastname())
                    .email(userDTO.getEmail())
                    .password(userDTO.getPassword())
                    .companyName(userDTO.getCompanyName())
                    .address(userDTO.getAddress())
                    .postalCode(userDTO.getPostalCode())
                    .city(userDTO.getCity())
                    .phoneNumber(userDTO.getPhoneNumber())
                    .employerId(userDTO.getEmployerId())
                    .build();
            case STUDENT -> Student.builder()
                    .id(userDTO.getId())
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastname())
                    .matricule(userDTO.getMatricule())
                    .email(userDTO.getEmail())
                    .password(userDTO.getPassword())
                    .build();
            default -> throw new IllegalArgumentException("Unknown role: " + userDTO.getRole());
        };
    }


    public static UserDTO toUserDTO(User user) {
        return switch (user) {
            case Manager manager -> ManagerDto.toManagerDto(manager);
            case Employer employer -> EmployerDto.toEmployerDto(employer);
            case Student student -> StudentDto.toStudentDto(student);
            default -> throw new IllegalArgumentException("Unknown user type: " + user.getClass().getName());
        };
    }
}
