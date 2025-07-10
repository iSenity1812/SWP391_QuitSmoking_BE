package com.swp391project.SWP391_QuitSmoking_BE.dto.follow;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponseDTO {
    private UUID followerId;
    private UUID followedId;
    private String followerUsername;
    private String followedUsername;
    private String followerProfilePicture;
    private String followedProfilePicture;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
