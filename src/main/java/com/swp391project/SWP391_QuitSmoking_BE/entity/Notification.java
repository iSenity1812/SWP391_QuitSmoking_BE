package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificationID", updatable = false, nullable = false)
    private Integer notificationId;

    @Column(name = "UserID", columnDefinition = "uuid")
    private UUID userId;

    @NotBlank(message = "NotificationType cannot be blank")
    @Column(name = "NotificationType", nullable = false)
    private String notificationType;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(name = "Title", length = 255, nullable = false)
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Column(name = "Content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public java.util.UUID getUserId() {
        return userId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public void setUserId(java.util.UUID userId) {
        this.userId = userId;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}