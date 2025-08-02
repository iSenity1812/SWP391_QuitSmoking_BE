package com.swp391project.SWP391_QuitSmoking_BE.dto.follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO chứa thống kê follow của một user")
public class UserFollowStatsDTO {
    @Schema(description = "ID của user", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;
    
    @Schema(description = "Username của user", example = "john_doe")
    private String username;
    
    @Schema(description = "Profile picture URL của user", example = "https://example.com/avatar.jpg")
    private String profilePicture;
    
    @Schema(description = "Số lượng followers", example = "150")
    private long followersCount;
    
    @Schema(description = "Số lượng người đang follow", example = "75")
    private long followingCount;
    
    @Schema(description = "Người dùng hiện tại có đang follow user này không", example = "true")
    private boolean isFollowedByCurrentUser; // Người dùng hiện tại có đang follow user này không
}
