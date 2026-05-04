package org.backend.qedu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExamRequest(
        @NotBlank String title,
        @NotBlank String classId,
        @NotBlank String className,
        @NotBlank String subjectId,
        @NotBlank String subjectName,
        @NotNull Integer maxPoints,
        LocalDate examDate,
        @NotBlank String status,
        @NotBlank String kind,
        String gradingJson,
        String questionsJson
) {}
