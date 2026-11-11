package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class UserCreateDTO {
    private String firstName;
    private String lastname;
    private String email;
    private String password;
    private Role role;
    private String matricule;
    private String companyName;
    private AdresseDTO adresseDTO;
    private String phoneNumber;
    private String employerWorkId;
    private Programe programe;
}