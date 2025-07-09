package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationid")
    private Long notificationId;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "from_userid")
    private UUID fromUserId;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "notification_type", length = 255)
    private String notificationType;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "userid")
    private UUID userId;
} 