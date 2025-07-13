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
@NoArgsConstructor
@AllArgsConstructor
public class NormalProfileResponseDTO {
    // === BASIC INFO ===
    private UUID userId;
    private String username;
    private String email;
    private String profilePicture;
    private Role role;
    private LocalDateTime createdAt;
    private Integer streakCount;
    
    // === CURRENT QUIT PLAN (Basic) ===
    private QuitPlanProfileDTO currentQuitPlan; // Sẽ chỉ có basic fields, không có premium fields
    
    // === BASIC QUIT STATS ===
    private QuitStatsProfileDTO quitStats; // Sẽ chỉ có basic stats, không có detailed fields
    
    // === BASIC CHART DATA (3 points) ===
    private List<DailyChartDataResponse> recentChartData; // 3 ngày gần nhất thay vì 7 ngày
    
    // === FOLLOW STATS ===
    private FollowStatsProfileDTO followStats;
    
    // === RECENT ACHIEVEMENTS (2 cái) ===
    private List<AchievementProfileDTO> recentAchievements; // Ít achievements hơn premium
    
    // Note: Normal member không có subscription info
}
