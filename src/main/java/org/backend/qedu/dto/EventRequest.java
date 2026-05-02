package org.backend.qedu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
public record EventRequest(
        @NotBlank String EventType,
        @NotBlank String EventTitle,
        @NotBlank String EventDescription,
        @NotNull LocalDateTime EventStartsAt,
        @NotNull LocalDateTime EventEndsAt,
        @NotBlank String location,
        @NotBlank String audience,
        @NotNull Long relatedTimetableId
){}
