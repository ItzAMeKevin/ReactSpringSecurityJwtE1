package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Location;
import com.lacouf.rsbjwt.service.dto.LocationDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDTO toDto(Location location);
}

