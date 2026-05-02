package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.backend.qedu.model.AttendanceStatus;

import java.time.LocalDate;

@Entity
@Table(name = "attendance_records")
@Getter
@Setter
public class AttendanceRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private Integer lessonIndex;

    @Column(nullable = false)
    private String classGroup;

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private String studentUsername;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String teacherUsername;

    @Column(nullable = false)
    private String teacherName;
    @Column(nullable = false)
    private String createdByTeacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus.Status attendanceStatus;
}