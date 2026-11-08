package com.lacouf.rsbjwt.service.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GenericDto {

    private String erreur;



}
