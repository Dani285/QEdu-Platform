package org.backend.qedu.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String classId,
        @NotBlank String className,
        @NotBlank String subjectId,
        @NotBlank String subjectName,
        @NotBlank String status,
        LocalDate startsAt,
        LocalDate deadline,
        Integer progress
) {}
