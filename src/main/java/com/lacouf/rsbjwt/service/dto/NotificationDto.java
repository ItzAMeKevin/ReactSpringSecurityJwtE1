package com.lacouf.rsbjwt.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.NotificationStatus;
import com.lacouf.rsbjwt.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;
    private String title;
    private String message;
    @JsonProperty("isRead")
    private boolean isRead;
    private Instant createdAt;
    private Instant sentAt;
    private NotificationType type;
    private NotificationStatus status;
    private Integer retryCount;
    private String studentName;
    private String studentFirstName;
    private String studentLastName;
    private String studentMatricule;
    private Long studentId;
    private String cvFileName;
    private Long cvId;
    private Instant uploadedAt;
    private Long managerId;
    private StudentCvDto studentCv;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentCvDto {
        private Long id;
        private String fileName;
    }
}
