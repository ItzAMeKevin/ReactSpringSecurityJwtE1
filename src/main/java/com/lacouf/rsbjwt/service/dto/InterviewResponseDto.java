package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.InterviewType;

import java.time.Instant;
import java.util.List;

public record InterviewResponseDto(Long id,
                                   Long candidatureId,
                                   InterviewType type,
                                   String videoLink,
                                   String address,
                                   String notes,
                                   Instant scheduledAt,
                                   List<InterviewSlotDto> slots
) {}
