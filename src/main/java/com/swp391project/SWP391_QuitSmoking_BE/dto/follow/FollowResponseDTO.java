package com.swp391project.SWP391_QuitSmoking_BE.dto.follow;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response DTO cho follow relationship")
public class FollowResponseDTO {
    @Schema(description = "ID của user đang follow", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID followerId;
    
    @Schema(description = "ID của user được follow", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID followedId;
    
    @Schema(description = "Username của user đang follow", example = "john_doe")
    private String followerUsername;
    
    @Schema(description = "Username của user được follow", example = "jane_smith")
    private String followedUsername;
    
    @Schema(description = "Profile picture URL của user đang follow", example = "https://example.com/avatar1.jpg")
    private String followerProfilePicture;
    
    @Schema(description = "Profile picture URL của user được follow", example = "https://example.com/avatar2.jpg")
    private String followedProfilePicture;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Thời gian follow được tạo", example = "2024-01-15 10:30:00")
    private LocalDateTime createdAt;
}
