package org.backend.qedu.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.backend.qedu.model.AttendanceStatus;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record AttendanceRequest(
  @NotNull LocalDate attendanceDate,
  @NotNull Integer lessonInx,
  @NotBlank String classGroup,
  @NotNull String subjectNames,
  @NotNull String studentUserName,
  @NotNull String studentName,
  @NotNull AttendanceStatus.Status attendanceStatus
){}
