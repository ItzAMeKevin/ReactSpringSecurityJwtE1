package com.lacouf.rsbjwt.service.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record InterviewSlotDto(Long id,
                               @NotNull LocalDate date,
                               @NotNull LocalTime startTime,
                               @NotNull LocalTime endTime,
                               Boolean selected
) {}
