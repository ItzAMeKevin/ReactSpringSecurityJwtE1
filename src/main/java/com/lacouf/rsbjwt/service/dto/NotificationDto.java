package com.lacouf.rsbjwt.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.NotificationType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;



@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class NotificationDto {

    private Long id;
    private NotificationType notificationType;
    private Long referenceId;
    private String message;
    private Status status;
    private Instant creationDate;

}
