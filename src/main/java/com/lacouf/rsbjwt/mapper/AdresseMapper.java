package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Adresse;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AdresseMapper {
    AdresseDTO toDto(Adresse adresse);
    Adresse toEntity(AdresseDTO adresseDTO);
}

