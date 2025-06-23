package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Notification;
import com.swp391project.SWP391_QuitSmoking_BE.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> getNotificationById(Integer id) {
        return notificationRepository.findById(id);
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification updateNotification(Integer id, Notification notificationDetails) {
        return notificationRepository.findById(id).map(notification -> {
            notification.setUserId(notificationDetails.getUserId());
            notification.setNotificationType(notificationDetails.getNotificationType());
            notification.setTitle(notificationDetails.getTitle());
            notification.setContent(notificationDetails.getContent());
            notification.setCreatedAt(notificationDetails.getCreatedAt());
            return notificationRepository.save(notification);
        }).orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }
}