package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Notification;
import com.swp391project.SWP391_QuitSmoking_BE.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Void> createNotification(@RequestBody Notification notification) {
        notificationService.createNotification(notification);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/send")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> sendAdminNotification(@RequestParam(required = false) UUID userId,
                                                       @RequestParam String title,
                                                       @RequestParam String content) {
        if (userId != null) {
            // Gửi cho 1 user
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setNotificationType("ADMIN");
            notification.setFromUserId(null); // Hệ thống
            notificationService.createNotification(notification);
            return ResponseEntity.ok("Đã gửi notification cho user " + userId);
        } else {
            // Broadcast cho tất cả user đang active
            List<User> users = userRepository.findAllByIsActiveTrue();
            for (User user : users) {
                Notification notification = new Notification();
                notification.setUserId(user.getUserId());
                notification.setTitle(title);
                notification.setContent(content);
                notification.setNotificationType("ADMIN");
                notification.setFromUserId(null); // Hệ thống
                notificationService.createNotification(notification);
            }
            return ResponseEntity.ok("Đã gửi notification cho tất cả user đang active");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countUnreadNotificationsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.countUnreadNotificationsByUser(userId));
    }

    @PutMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
} 