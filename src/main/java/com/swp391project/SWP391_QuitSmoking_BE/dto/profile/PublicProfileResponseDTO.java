package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;

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
public class PublicProfileResponseDTO {
    // === PUBLIC BASIC INFO ===
    private UUID userId;
    private String username;
    private String profilePicture;
    private Role role; // Hiển thị để biết loại member
    private String memberSince; // Calculated from createdAt (e.g., "6 tháng")
    // Không có email và isActive vì đây là thông tin private
    
    // === PUBLIC STATS ===
    private PublicStatsDTO publicStats;
    
    // === FOLLOW INFO ===
    private FollowStatsProfileDTO followStats; // Bao gồm interaction status
    
    // === PUBLIC ACHIEVEMENTS (Chỉ những cái đã share) ===
    private List<AchievementProfileDTO> sharedAchievements;
    
    // === SUBSCRIPTION STATUS (Basic) ===
    private PublicSubscriptionStatusDTO subscriptionStatus; // Chỉ show premium badge, không show details
    
    // Inner classes for public-specific data
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicStatsDTO {
        private Integer streakCount; // Nếu user cho phép public
        private Boolean isOnQuitJourney; // Có đang trong hành trình cai thuốc không
        private Integer totalAchievements; // Tổng số achievements
        private Boolean isActiveRecently; // Hoạt động gần đây (7 ngày)
        // Không có detailed stats như số điếu, tiền tiết kiệm
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicSubscriptionStatusDTO {
        private Boolean isPremium;
        private String premiumSince; // "Tháng 6/2024" - không cần chi tiết
        private Boolean showsPremiumBadge;
        // Không có thông tin về giá, thời hạn, package details
    }
}
