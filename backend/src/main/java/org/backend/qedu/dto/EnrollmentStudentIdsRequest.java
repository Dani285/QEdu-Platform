package org.backend.qedu.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EnrollmentStudentIdsRequest(@NotNull List<Long> studentUserIds) {
}
