package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "qedu_exams")
@Getter
@Setter
public class QEduExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String classId;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private String subjectId;

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private Integer maxPoints;

    private LocalDate examDate;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String kind;


    @Column(length = 6000)
    private String gradingJson;

    @Column(length = 16000)
    private String questionsJson;

    @Column(nullable = false)
    private String teacherUsername;
}
