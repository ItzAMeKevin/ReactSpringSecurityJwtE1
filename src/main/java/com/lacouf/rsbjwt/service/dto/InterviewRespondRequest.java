package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.InterviewStatus;

public record InterviewRespondRequest(
        InterviewStatus status,
        Long selectedSlotId,
        String message
) {}
