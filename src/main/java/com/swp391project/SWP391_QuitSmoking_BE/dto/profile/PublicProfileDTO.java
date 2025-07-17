package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class PublicProfileDTO {
    // Core Information
    private String userId;
    private String username;
    private String profilePicture;
    private String role;
    private String memberSince;

    // Public Statistics
    private Integer streakCount;
    private String quitJourneyStatus;
    private long totalAchievementsEarned;
    private long followersCount;
    private long followingCount;

    // Shared Achievements
    private List<SharedAchievementDTO> sharedAchievements;

    // Subscription Status
    private boolean isPremium;
    private Date premiumSince;
    private boolean hasPremiumBadge;

    @Data
    @Builder
    public static class SharedAchievementDTO {
        private String name;
        private String iconUrl;
        private Date dateAchieved;
    }
}
