package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.InterviewType;

import java.time.Instant;
import java.util.List;

public record EmployerInterviewDto(Long id,
                                   Long candidatureId,
                                   Long offerId,
                                   String offerTitle,
                                   String studentFirstName,
                                   String studentLastName,
                                   String studentEmail,
                                   InterviewType type,
                                   String videoLink,
                                   AdresseDTO address,
                                   String notes,
                                   Instant scheduledAt,
                                   List<InterviewSlotDto> slots
) {}
