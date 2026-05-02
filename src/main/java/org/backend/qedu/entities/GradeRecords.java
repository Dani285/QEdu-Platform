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
    private String studentUsername;
    private String studentName;
    private String classGroup;
    private String subjectName;

    private Integer grade;
    private Double weight_grades = 1.0;
    private String notes;
    private String teacherUsername;
    private String teacherName;
    private LocalDateTime createdTime;
}
