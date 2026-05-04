package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qedu_study_materials")
@Getter
@Setter
public class StudyMaterial {

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
    private String materialType;

    @Column(nullable = false)
    private String status;

    private LocalDate validFrom;
    private LocalDate validTo;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String teacherUsername;
}
