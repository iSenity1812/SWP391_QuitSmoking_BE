package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;

import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailyChartDataResponse;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PremiumProfileResponseDTO {
    // Basic info
    private UUID userId;
    private String username;
    private String email;
    private String profilePicture;
    private Role role;
    private LocalDateTime createdAt;
    private Integer streakCount;

    // Premium features
    private QuitPlanProfileDTO currentQuitPlan;
    private QuitStatsProfileDTO quitStats;
    private List<DailyChartDataResponse> weeklyChartData;
    private FollowStatsProfileDTO followStats;
    private SubscriptionProfileDTO currentSubscription;
    private List<AchievementProfileDTO> recentAchievements;
}