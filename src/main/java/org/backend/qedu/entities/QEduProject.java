package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qedu_projects")
@Getter
@Setter
public class QEduProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private String classId;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private String subjectId;

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private String status;

    private LocalDate startsAt;
    private LocalDate deadline;

    private Integer progress;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String teacherUsername;
}
