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
}
