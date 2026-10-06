package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Programe;
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
}