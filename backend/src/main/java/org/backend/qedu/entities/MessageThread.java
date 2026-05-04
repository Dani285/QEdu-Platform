package org.backend.qedu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "qedu_message_threads")
@Getter
@Setter
public class MessageThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 2000)
    private String lastMessage;

    @Column(nullable = false)
    private String authorUsername;

    @Column(nullable = false)
    private String authorRole;

    /** JSON array string, e.g. ["TEACHER","STUDENT"] or ["ALL"] */
    @Column(nullable = false, length = 500)
    private String audienceJson;

    /** JSON array of class ids/names or empty array */
    @Column(length = 1000)
    private String classTargetsJson;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
