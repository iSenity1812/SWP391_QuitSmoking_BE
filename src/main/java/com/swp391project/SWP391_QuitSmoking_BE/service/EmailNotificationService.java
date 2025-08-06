package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {
    
    private final EmailService emailService;
    
    /**
     * Gửi email chúc mừng khi user đạt achievement
     */
    @Async
    public void sendAchievementEmail(User user, Achievement achievement) {
        try {
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("username", user.getUsername());
            templateVariables.put("achievementName", achievement.getName());
            templateVariables.put("achievementDescription", achievement.getDescription());
            templateVariables.put("achievementIcon", achievement.getIconUrl());
            templateVariables.put("achievementType", achievement.getAchievementType().name());
            templateVariables.put("milestoneValue", achievement.getMilestoneValue());
            templateVariables.put("achievedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
            EmailDetail emailDetail = new EmailDetail(
                user.getEmail(),
                "🎉 Chúc mừng! Bạn vừa đạt được thành tựu mới - QuitTogether",
                null,
                "achievementTemplate.html",
                templateVariables
            );
            
            emailService.sendEmail(emailDetail);
            log.info("Achievement email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send achievement email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }
    
    /**
     * Gửi email nhắc nhở hàng ngày
     */
    @Async
    public void sendDailyReminderEmail(User user, int daysQuit, int cigarettesNotSmoked, double moneySaved) {
        try {
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("username", user.getUsername());
            templateVariables.put("daysQuit", daysQuit);
            templateVariables.put("cigarettesNotSmoked", cigarettesNotSmoked);
            templateVariables.put("moneySaved", String.format("%.0f", moneySaved));
            templateVariables.put("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            EmailDetail emailDetail = new EmailDetail(
                user.getEmail(),
                "🌱 Nhắc nhở hàng ngày - Tiếp tục hành trình bỏ thuốc - QuitTogether",
                null,
                "dailyReminderTemplate.html",
                templateVariables
            );
            
            emailService.sendEmail(emailDetail);
            log.info("Daily reminder email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send daily reminder email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }
    
    /**
     * Gửi email động viên hàng ngày
     */
    @Async
    public void sendDailyMotivationEmail(User user, String motivationMessage, int streakDays) {
        try {
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("username", user.getUsername());
            templateVariables.put("motivationMessage", motivationMessage);
            templateVariables.put("streakDays", streakDays);
            templateVariables.put("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            EmailDetail emailDetail = new EmailDetail(
                user.getEmail(),
                "💪 Động viên hàng ngày - Bạn đang làm rất tốt! - QuitTogether",
                null,
                "dailyMotivationTemplate.html",
                templateVariables
            );
            
            emailService.sendEmail(emailDetail);
            log.info("Daily motivation email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send daily motivation email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }
    
    /**
     * Gửi email chào mừng khi user đăng ký
     */
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("username", user.getUsername());
            templateVariables.put("registrationDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            EmailDetail emailDetail = new EmailDetail(
                user.getEmail(),
                "🎉 Chào mừng bạn đến với QuitTogether! - Bắt đầu hành trình bỏ thuốc",
                null,
                "welcomeTemplate.html",
                templateVariables
            );
            
            emailService.sendEmail(emailDetail);
            log.info("Welcome email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }
    
    /**
     * Gửi email thông báo milestone quan trọng
     */
    @Async
    public void sendMilestoneEmail(User user, String milestoneType, int milestoneValue) {
        try {
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("username", user.getUsername());
            templateVariables.put("milestoneType", milestoneType);
            templateVariables.put("milestoneValue", milestoneValue);
            templateVariables.put("achievedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
            EmailDetail emailDetail = new EmailDetail(
                user.getEmail(),
                "🏆 Chúc mừng milestone mới! - QuitTogether",
                null,
                "milestoneTemplate.html",
                templateVariables
            );
            
            emailService.sendEmail(emailDetail);
            log.info("Milestone email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send milestone email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }
} 