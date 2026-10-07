package com.lacouf.rsbjwt.service.mapper;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GestionnaireMapper {

    @Mapping(source = "student.firstName", target = "studentFirstName")
    @Mapping(source = "student.lastName", target = "studentLastName")
    @Mapping(source = "student.matricule", target = "studentMatricule")
    PendingCvDto toPendingCvDto(StudentCv cv);

    @Mapping(source = "read", target = "isRead")
    NotificationDto toNotificationDto(Notification notification);
}
