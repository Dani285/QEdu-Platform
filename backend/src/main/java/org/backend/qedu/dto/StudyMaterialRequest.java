package org.backend.qedu.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record StudyMaterialRequest(
        @NotBlank String title,
        @NotBlank String classId,
        @NotBlank String className,
        @NotBlank String subjectId,
        @NotBlank String subjectName,
        @NotBlank String materialType,
        @NotBlank String status,
        LocalDate validFrom,
        LocalDate validTo
) {}
