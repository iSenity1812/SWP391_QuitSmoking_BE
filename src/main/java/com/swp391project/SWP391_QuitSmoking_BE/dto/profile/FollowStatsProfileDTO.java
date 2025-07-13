package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatsProfileDTO {
    private Long followersCount;
    private Long followingCount;

    // Public profile only
    private Boolean isFollowedByCurrentUser;
    private Boolean canFollow;
}