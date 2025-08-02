package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.scheduler.AchievementScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Service để trigger achievement check real-time khi có events quan trọng
 */
@Service
@RequiredArgsConstructor
public class AchievementTriggerService {
    private final AchievementScheduler achievementScheduler;

    /**
     * Trigger achievement check ngay lập tức khi user thực hiện hành động quan trọng
     */
    public void triggerAchievementCheck(UUID memberId, String eventType) {
        System.out.println("[AchievementTriggerService] Trigger achievement check for memberId: " + memberId + ", event: " + eventType);
        
        // Gọi scheduler để check achievement ngay lập tức thay vì đợi cron job
        achievementScheduler.triggerAchievementCheckForUser(memberId);
    }

    /**
     * Trigger khi user hoàn thành daily goal
     */
    public void onDailyGoalCompleted(UUID memberId) {
        triggerAchievementCheck(memberId, "DAILY_GOAL_COMPLETED");
    }

    /**
     * Trigger khi user resist craving
     */
    public void onCravingResisted(UUID memberId) {
        triggerAchievementCheck(memberId, "CRAVING_RESISTED");
    }

    /**
     * Trigger khi user update smoking data
     */
    public void onSmokingDataUpdated(UUID memberId) {
        triggerAchievementCheck(memberId, "SMOKING_DATA_UPDATED");
    }

    /**
     * Trigger khi user thêm daily summary
     */
    public void onDailySummaryAdded(UUID memberId) {
        triggerAchievementCheck(memberId, "DAILY_SUMMARY_ADDED");
    }

    /**
     * Trigger khi user login
     */
    public void onUserLogin(UUID memberId) {
        triggerAchievementCheck(memberId, "USER_LOGIN");
    }

    /**
     * Trigger khi user refresh trang achievements
     */
    public void onAchievementPageRefresh(UUID memberId) {
        triggerAchievementCheck(memberId, "ACHIEVEMENT_PAGE_REFRESH");
    }

    /**
     * Trigger khi user update profile
     */
    public void onProfileUpdate(UUID memberId) {
        triggerAchievementCheck(memberId, "PROFILE_UPDATE");
    }

    /**
     * Trigger khi user thay đổi quit plan
     */
    public void onQuitPlanChange(UUID memberId) {
        triggerAchievementCheck(memberId, "QUIT_PLAN_CHANGE");
    }
}
