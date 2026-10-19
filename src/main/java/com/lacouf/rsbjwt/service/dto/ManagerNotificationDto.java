package com.lacouf.rsbjwt.service.dto;

import java.time.Instant;

public record ManagerNotificationDto(
        Long id,
        String title,
        String message,
        boolean isRead,
        Instant createdAt,
        Long offerId,
        String type,
        String company,
        String offerTitle,
        String program
) {}
