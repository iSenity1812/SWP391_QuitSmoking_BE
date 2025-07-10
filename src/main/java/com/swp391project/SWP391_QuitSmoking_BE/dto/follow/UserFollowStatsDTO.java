package com.swp391project.SWP391_QuitSmoking_BE.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollowStatsDTO {
    private UUID userId;
    private String username;
    private String profilePicture;
    private long followersCount;
    private long followingCount;
    private boolean isFollowedByCurrentUser; // Người dùng hiện tại có đang follow user này không
}
