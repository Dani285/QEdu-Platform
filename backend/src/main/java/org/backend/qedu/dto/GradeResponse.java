package org.backend.qedu.dto;

import org.backend.qedu.entities.GradeRecords;

import java.time.LocalDateTime;

public record GradeResponse(
        Long id,
        String studentUsername,
        String studentName,
        String classGroup,
        String subjectName,
        Integer grade,
        Double weightGrades,
        String notes,
        String teacherUsername,
        String teacherName,
        LocalDateTime createdTime
) {
    public static GradeResponse from(GradeRecords g) {
        return new GradeResponse(
                g.getID(),
                g.getStudentUsername(),
                g.getStudentName(),
                g.getClassGroup(),
                g.getSubjectName(),
                g.getGrade(),
                g.getWeight_grades(),
                g.getNotes(),
                g.getTeacherUsername(),
                g.getTeacherName(),
                g.getCreatedTime()
        );
    }
}
