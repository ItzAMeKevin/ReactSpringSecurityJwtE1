package com.lacouf.rsbjwt.service.mapper;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.Student;
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
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.firstName", target = "studentName")
    @Mapping(source = "student.firstName", target = "studentFirstName")
    @Mapping(source = "student.lastName", target = "studentLastName")
    @Mapping(source = "student.matricule", target = "studentMatricule")
    @Mapping(source = "studentCv.fileName", target = "cvFileName")
    @Mapping(source = "studentCv.id", target = "cvId")
    @Mapping(source = "studentCv.uploadedAt", target = "uploadedAt")
    @Mapping(source = "manager.id", target = "managerId")
    NotificationDto toNotificationDto(Notification notification);

    default NotificationDto.StudentCvDto toStudentCvDto(StudentCv cv) {
        if (cv == null) {
            return null;
        }
        return NotificationDto.StudentCvDto.builder()
                .id(cv.getId())
                .fileName(cv.getFileName())
                .build();
    }

    default NotificationDto toNotificationDtoWithStudentCv(Notification notification) {
        NotificationDto dto = toNotificationDto(notification);
        dto.setStudentCv(toStudentCvDto(notification.getStudentCv()));
        
        // If student is null but studentCv exists, get student info from studentCv
        if (dto.getStudentFirstName() == null && notification.getStudentCv() != null 
            && notification.getStudentCv().getStudent() != null) {
            Student student = notification.getStudentCv().getStudent();
            dto.setStudentFirstName(student.getFirstName());
            dto.setStudentLastName(student.getLastName());
            dto.setStudentMatricule(student.getMatricule());
            dto.setStudentId(student.getId());
            dto.setStudentName(student.getFirstName());
        }
        
        return dto;
    }

    default NotificationDto toNotificationDtoForManager(Notification notification) {
        NotificationDto dto = toNotificationDto(notification);
        
        // For manager notifications, get student info from studentCv
        if (notification.getStudentCv() != null && notification.getStudentCv().getStudent() != null) {
            Student student = notification.getStudentCv().getStudent();
            dto.setStudentFirstName(student.getFirstName());
            dto.setStudentLastName(student.getLastName());
            dto.setStudentMatricule(student.getMatricule());
            dto.setStudentId(student.getId());
            dto.setStudentName(student.getFirstName());
        }
        
        return dto;
    }
}
