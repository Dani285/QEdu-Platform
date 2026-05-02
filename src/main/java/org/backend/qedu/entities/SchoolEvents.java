package org.backend.qedu.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "qeduschool_events")
@Getter
@Setter
public class SchoolEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long ID;

    @Column(nullable = false)

    private String EventType;

    @Column(nullable = false, length=10)

    private String EventTitle;

    @Column(nullable = false, length=20)

    private String EventDescription;

    @Column(nullable = false)

    private String location;

    @Column(nullable = false)

    private String Audience;

    @Column(nullable = false)
    private Long setRelatedTimetableId;

    @Column(nullable = false)

    private LocalDateTime eventStartTime;

    @Column(nullable = false)

    private LocalDateTime eventEndTime;

    @Column(nullable = false)

    private String createdByUser;
}
