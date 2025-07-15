package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class NormalProfileDTO {
    // Core Information
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

    // Quit Statistics
    private long daysWithoutSmoking;
    private long cigarettesAvoided;
    private BigDecimal moneySaved;
    private List<DailyChartData> dailyChartData;

    // Follow Statistics
    private long followersCount;
    private long followingCount;

    // Achievements
    private List<AchievementDTO> last3Achievements;

    @Data
    @Builder
    public static class DailyChartData {
        private LocalDate date;
        private long cigarettesSmoked;
    }

    @Data
    @Builder
    public static class AchievementDTO {
        private Long id;
        private String name;
        private String iconUrl;
        private Date dateAchieved;
    }
}
