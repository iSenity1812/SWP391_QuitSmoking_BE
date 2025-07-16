package com.swp391project.SWP391_QuitSmoking_BE.scheduler;

import com.swp391project.SWP391_QuitSmoking_BE.service.AchievementService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.UUID;

@Configuration
@AllArgsConstructor
public class AchievementScheduler {
    private final AchievementService achievementService;

    // Giảm tần suất check scheduler xuống 5 phút một lần vì giờ có real-time trigger
    @Scheduled(cron = "0 */5 * * * *") // Run every 5 minutes
    public void checkTimeBasedAchievements() {
        System.out.println("[AchievementScheduler] Đang kiểm tra achievement định kỳ...");
        achievementService.checkTimeBasedAchievements();
    }

    /**
     * Method để trigger achievement check cho một user cụ thể (gọi real-time)
     */
    public void triggerAchievementCheckForUser(UUID memberId) {
        System.out.println("[AchievementScheduler] Trigger achievement check cho memberId: " + memberId);
        achievementService.checkAndUnlockAchievements(memberId);
    }
}
