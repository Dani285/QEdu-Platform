package org.backend.qedu.dto;

import jakarta.validation.constraints.NotBlank;

public record ClassSubjectRequest(
        @NotBlank String classId,
        @NotBlank String className,
        @NotBlank String subjectId,
        @NotBlank String subjectName,
        @NotBlank String teacherUsername
) {}
