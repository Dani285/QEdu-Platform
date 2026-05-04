package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "qedugrade_records")
@Getter
@Setter

public class GradeRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;


    @Column(nullable = false)
    private String studentUsername;
    @Column(nullable = false)
    private String studentName;
    @Column(nullable = false)
    private String classGroup;
    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private Integer grade;
    @Column(nullable = false)
    private Double weight_grades = 1.0;
    @Column(nullable = false)
    private String notes;
    @Column(nullable = false)
    private String teacherUsername;
    @Column(nullable = false)
    private String teacherName;
    @Column(nullable = false)
    private LocalDateTime createdTime;
}
