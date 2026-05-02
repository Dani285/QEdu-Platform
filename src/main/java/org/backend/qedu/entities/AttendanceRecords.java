package org.backend.qedu.entities;
import jakarta.validation.constraints.NotNull;
import org.backend.qedu.model.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "attendance_records")
@Getter
@Setter
public class AttendanceRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private LocalDate attendanceDate;
    private Integer lessonIndex;

    private String classGroup;
    private String subjectName;

    private String studentUsername;
    private String studentName;

    @Enumerated(EnumType.STRING)

    private String teacherName;
    private String createdByTeacher;

    @Column(nullable = false)
    private AttendanceStatus.Status attendanceStatus;
}
