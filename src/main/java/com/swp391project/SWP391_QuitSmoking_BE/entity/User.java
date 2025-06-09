package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "\"User\"")
public class User {
    @Id
    @Column(name = "UserID")
    private UUID userId;

    @Column(name = "Username", unique = true, nullable = false)
    private String username;

    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "RoleID", nullable = false)
    private Integer roleId;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    @Column(name = "profilePicture")
    private String profilePicture;

    @Column(name = "NotificationSetting", columnDefinition = "json")
    private String notificationSetting;

    @ManyToOne
    @JoinColumn(name = "RoleID", insertable = false, updatable = false)
    private Role role;
}