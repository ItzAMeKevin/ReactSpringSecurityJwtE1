package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.InterviewType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ScheduleInterviewRequestDto(
        @NotNull Long candidatureId,
        @NotNull InterviewType type,
        String videoLink,
        String address,
        @Size(max = 500) String notes,
        @NotEmpty @Valid List<InterviewSlotDto> slots
) {}
