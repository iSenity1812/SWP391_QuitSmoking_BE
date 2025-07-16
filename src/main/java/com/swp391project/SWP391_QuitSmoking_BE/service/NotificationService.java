package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Notification;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @Transactional
    public void createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
//        notificationRepository.save(notification);
        Notification savedNotification = notificationRepository.save(notification);
        messagingTemplate.convertAndSendToUser(
                notification.getUserId().toString(),
                "/topic/notifications",
                savedNotification
        );
    }

    public List<Notification> getNotificationsByUser(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotificationsByUser(UUID userId) {
        return notificationRepository.findByIsReadFalseAndUserId(userId);
    }

    public long countUnreadNotificationsByUser(UUID userId) {
        return notificationRepository.countByIsReadFalseAndUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    public void deleteNotificationByUserIdAndContent(UUID userId, String content) {
        notificationRepository.deleteByUserIdAndContent(userId, content);
    }

    /**
     * Gửi WebSocket notification đặc biệt cho achievement
     */
    @Async
    public void sendAchievementNotification(UUID userId, Achievement achievement) {
        Map<String, Object> achievementNotification = new HashMap<>();
        achievementNotification.put("type", "ACHIEVEMENT");
        achievementNotification.put("title", "Chúc mừng! 🎉");
        achievementNotification.put("message", "Bạn vừa đạt được thành tựu: " + achievement.getName());
        achievementNotification.put("achievementId", achievement.getAchievementId());
        achievementNotification.put("achievementName", achievement.getName());
        achievementNotification.put("achievementDescription", achievement.getDescription());
        achievementNotification.put("achievementIcon", achievement.getIconUrl());
        achievementNotification.put("achievementType", achievement.getAchievementType().name());
        achievementNotification.put("milestoneValue", achievement.getMilestoneValue());
        achievementNotification.put("timestamp", LocalDateTime.now());
        
        // Gửi qua WebSocket cho frontend hiển thị toast
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/topic/achievements",
                achievementNotification
        );
    }
} 