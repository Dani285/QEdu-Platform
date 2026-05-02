package org.backend.qedu.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TimeTableRequest(
        @NotNull Integer dayOfTheWeek,
        @NotNull Integer lessonInd,
        @NotNull String classGroups,
        @NotNull String subjectNames,
        @NotNull String teacherUsername,
        @NotNull String teacherName,
        @NotNull String classRoomName,
        @NotNull LocalTime lessonStartsAt,
        @NotNull LocalTime lessonEndsAt) {}
