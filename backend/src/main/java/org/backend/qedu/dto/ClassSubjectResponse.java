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
    public static ClassSubjectResponse from(ClassSubjectAssignment a, long studentCount) {
        return new ClassSubjectResponse(
                a.getId(),
                a.getClassId(),
                a.getClassName(),
                a.getSubjectId(),
                a.getSubjectName(),
                a.getTeacherUsername(),
                a.getTeacherName(),
                studentCount
        );
    }
}
