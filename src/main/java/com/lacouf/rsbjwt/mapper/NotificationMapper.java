package com.lacouf.rsbjwt.mapper;


import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {


    NotificationDto toDto(Notification notification);

    List<NotificationDto> toDtoList(List<Notification> notifications);

    @Mapping(target = "id", ignore = true)
    Notification toEntity(NotificationDto notificationDto);



}
