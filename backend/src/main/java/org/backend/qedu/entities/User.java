package org.backend.qedu.entities;
import lombok.Builder;
import  org.backend.qedu.model.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "qedu_users")
@Getter
@Setter

public class User {

    public User(){}
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)

    private Long ID;

    @Column(nullable = false, unique =true)

    private String userName;

    @Column(nullable = false)

    private String password;

    @Column(nullable = false)

    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    private Roles roles;

    private String classGroups;

    private boolean enabled = true;

    private boolean isVerified;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
