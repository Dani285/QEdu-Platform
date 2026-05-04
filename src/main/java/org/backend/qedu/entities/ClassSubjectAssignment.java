package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "qedu_class_subject")
@Getter
@Setter
public class ClassSubjectAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String classId;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private String subjectId;

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private String teacherUsername;

    @Column(nullable = false)
    private String teacherName;
}
