package org.backend.qedu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GradeUpdateRequest(
        @Min(1) @Max(5) Integer grade,
        String notes,
        @Min(0) Double weightGrades
) {}
