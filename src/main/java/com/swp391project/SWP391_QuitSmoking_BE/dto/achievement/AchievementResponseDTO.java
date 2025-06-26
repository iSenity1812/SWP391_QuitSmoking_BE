package com.swp391project.SWP391_QuitSmoking_BE.dto.achievement;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponseDTO {

    private Long achievementId;
    private String title;
    private String description;
    private Integer targetValue;
    private String category;
    private String badgeUrl;
    private Integer points;
    private LocalDateTime createdAt;
    private boolean isActive;
}