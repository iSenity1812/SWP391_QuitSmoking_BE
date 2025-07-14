package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Notification;
import com.swp391project.SWP391_QuitSmoking_BE.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Async
    @Transactional
    public void createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        notificationRepository.save(notification);
        
        // TODO: Implement push notification service later
        // try {
        //     pushNotificationService.sendNotificationToUser(
        //         notification.getUserId(),
        //         notification.getTitle(),
        //         notification.getContent(),
        //         notification.getNotificationType()
        //     );
        // } catch (Exception e) {
        //     System.err.println("Failed to send push notification: " + e.getMessage());
        // }
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
} 