package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "qedutimetable_entries")
@Getter
@Setter

public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long ID;

    @Column(nullable = false)

    private Integer dayOfTheWeek;

    @Column(nullable = false)

    private Integer lessonIdx;

    @Column(nullable = false)

    private String classGroups;

    @Column(nullable = false)

    private String subjectNames;

    @Column(nullable = false)

    private String teacherUserName;

    @Column(nullable = false)

    private String teacherName;

    @Column(nullable = false)

    private String classRoomName;

    @Column(nullable = false)

    private LocalTime lessonStartsAt;

    @Column(nullable = false)

    private LocalTime lessonEndsAt;
}
