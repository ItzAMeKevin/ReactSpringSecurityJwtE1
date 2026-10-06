package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Adresse;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdresseMapper {
    @Mapping(source = "pay", target = "pays")
    AdresseDTO toDto(Adresse adresse);

    @Mapping(source = "pays", target = "pay")
    Adresse toEntity(AdresseDTO adresseDTO);
}

