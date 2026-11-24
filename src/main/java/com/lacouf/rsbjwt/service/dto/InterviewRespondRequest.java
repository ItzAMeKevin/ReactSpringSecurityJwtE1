package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Status;

public record InterviewRespondRequest(
        Status status,
        Long selectedSlotId,
        String message
) {}
