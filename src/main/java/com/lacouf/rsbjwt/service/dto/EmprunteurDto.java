package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.auth.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmprunteurDto extends UserDTO {
    private LocalDate since;

    @Builder
    public EmprunteurDto(Long id, String firstName, String lastname, String email, Role role, LocalDate since) {
        super(id, firstName, lastname, email, role);
        this.since = since;
    }

    public EmprunteurDto() {}

    public static EmprunteurDto create(Employer emprunteur) {
        return EmprunteurDto.builder()
                .id(emprunteur.getId())
                .firstName(emprunteur.getFirstName())
                .lastname(emprunteur.getLastName())
                .email(emprunteur.getEmail())
                .role(emprunteur.getRole())
                .since(emprunteur.getSince())
                .build();



    }

    public static EmprunteurDto empty() {
        return new EmprunteurDto();
    }
}
