package org.backend.qedu.dto;

import org.backend.qedu.model.Roles;
public record StatisticsResponse(
        Roles role,
        Double avgGrades,
        Long totalGrades,
        Long absences,
        Long lateArrivals,
        Long totalStudents,
        Long totalTeachers,
        Long totalAdmins,
        Long taughtClasses,
        Long lessons,
        Long upcomingEvents
) {}
