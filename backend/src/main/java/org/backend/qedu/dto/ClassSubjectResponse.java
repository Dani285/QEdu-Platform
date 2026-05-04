package org.backend.qedu.dto;

import org.backend.qedu.entities.ClassSubjectAssignment;

public record ClassSubjectResponse(
        Long id,
        String classId,
        String className,
        String subjectId,
        String subjectName,
        String teacherUsername,
        String teacherName,
        long studentCount
) {
    public static ClassSubjectResponse from(ClassSubjectAssignment assignment, long studentCount) {
        return new ClassSubjectResponse(
                assignment.getId(),
                assignment.getClassId(),
                assignment.getClassName(),
                assignment.getSubjectId(),
                assignment.getSubjectName(),
                assignment.getTeacherUsername(),
                assignment.getTeacherName(),
                studentCount
        );
    }
}
