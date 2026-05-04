package org.backend.qedu.dto;

import jakarta.validation.constraints.*;

public record GradeCreateRequest(
        @NotBlank String studentUsername,
        @NotBlank String studentName,
        @NotBlank String classGroup,
        @NotBlank String subjectName,
        @NotNull @Min(1) @Max(5) Integer grade,
        @NotBlank String notes,
        Double weightGrades
) {
    public GradeCreateRequest {
        weightGrades = weightGrades != null ? weightGrades : 1.0;
    }
}
