package com.lacouf.rsbjwt.service.dto;


import com.lacouf.rsbjwt.mapper.AdresseMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor 
@NoArgsConstructor
@Setter
@Getter 
public class UserCreateDTO {
    private AdresseMapper adresseMapper;

    private Long id;
    private String firstName;
    private String lastname;
    private String email;
    private String password;
    private Role role;
    private String matricule;
    private String companyName;
    private AdresseDTO adresseDTO;
    private String phoneNumber;
    private String employerId;
    private Programe programe;

    public User toEntity(UserCreateDTO userCreateDTO) {
        return switch (userCreateDTO.getRole()) {
            case MANAGER -> Manager.builder()
                    .id(userCreateDTO.getId())
                    .firstName(userCreateDTO.getFirstName())
                    .password(userCreateDTO.getPassword())
                    .matricule(userCreateDTO.getMatricule())
                    .lastName(userCreateDTO.getLastname())
                    .email(userCreateDTO.getEmail())
                    .password(userCreateDTO.getPassword())
                    .build();
            case EMPLOYER -> Employer.builder()
                    .id(userCreateDTO.getId())
                    .firstName(userCreateDTO.getFirstName())
                    .lastName(userCreateDTO.getLastname())
                    .email(userCreateDTO.getEmail())
                    .password(userCreateDTO.getPassword())
                    .companyName(userCreateDTO.getCompanyName())
                    .adresse(adresseMapper.toEntity(userCreateDTO.getAdresseDTO()))
                    .phoneNumber(userCreateDTO.getPhoneNumber())
                    .employerWorkId(userCreateDTO.getEmployerId())
                    .build();
            case STUDENT -> Student.builder()
                    .id(userCreateDTO.getId())
                    .firstName(userCreateDTO.getFirstName())
                    .lastName(userCreateDTO.getLastname())
                    .matricule(userCreateDTO.getMatricule())
                    .email(userCreateDTO.getEmail())
                    .programe(userCreateDTO.getPrograme())
                    .password(userCreateDTO.getPassword())
                    .build();
            default -> throw new IllegalArgumentException("Unknown role: " + userCreateDTO.getRole());
        };
    }


    public static UserCreateDTO toUserDTO(User user) {
        return switch (user) {
            case Manager manager -> ManagerCreateDto.toManagerDto(manager);
            case Employer employer -> EmployerCreateDto.toEmployerDto(employer);
            case Student student -> StudentCreateDto.toStudentDto(student);
            default -> throw new IllegalArgumentException("Unknown user type: " + user.getClass().getName());
        };
    }
}
