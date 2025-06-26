package com.swp391project.SWP391_QuitSmoking_BE.dto.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long notificationId;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private String actionUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}