package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class PremiumProfileDTO {
    // Basic Information
    private String userId;
    private String username;
    private String email;
    private String profilePicture;
    private String role;
    private Date accountCreationDate;
    private int currentStreakCount;

    // Quit Plan Details
    private Integer currentQuitPlanId;
    private String quitPlanStatus;
    private String progressSnapshot;

    // Quit Statistics
    private long daysWithoutSmoking;
    private long cigarettesAvoided;
    private BigDecimal moneySaved;
    private BigDecimal totalMoneySaved;
    private long totalCigarettesSmokedSinceStart;
    private long totalCravings;
    private double averageDailyCravings;
    private List<DailyChartData> dailyChartData;

    // Follow Statistics
    private long followersCount;
    private long followingCount;

    // Subscription Information
    private Long subscriptionId;
    private String packageName;
    private BigDecimal price;
    private Date subscriptionStartDate;
    private Date subscriptionEndDate;
    private long daysRemaining;
    private String subscriptionStatus;

    // Achievements
    private List<AchievementDTO> last5Achievements;

    @Data
    @Builder
    public static class DailyChartData {
        private LocalDate date;
        private long cigarettesSmoked;
        private long cravings;
    }

    @Data
    @Builder
    public static class AchievementDTO {
        private Long id;
        private String name;
        private String iconUrl;
        private String detailedDescription;
        private Date dateAchieved;
        private boolean isShared;
    }
}
