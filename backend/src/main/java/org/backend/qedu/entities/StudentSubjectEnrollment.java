package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "qedu_student_subject_enrollment",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_enrollment_student_class_subject",
                columnNames = {"student_id", "class_subject_id"}
        )
)
@Getter
@Setter
public class StudentSubjectEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_subject_id", nullable = false)
    private ClassSubjectAssignment classSubject;
}
