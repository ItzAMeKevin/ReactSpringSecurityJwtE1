package com.lacouf.rsbjwt.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.NotificationMessage;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.model.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;



@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class NotificationDto extends GenericDto {

    private Long id;
    private User user;
    private NotificationType notificationType;
    private Long referenceId;
    private NotificationMessage message;
    private Status status;
    private Instant creationDate;

}
