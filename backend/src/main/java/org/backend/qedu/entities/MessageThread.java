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

    @Column(nullable = false, length = 1000)
    private String audienceJson;

    @Column(length = 1000)
    private String classTargetsJson;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
