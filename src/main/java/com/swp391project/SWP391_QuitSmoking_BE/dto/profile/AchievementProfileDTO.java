package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementProfileDTO {
    private Long achievementId;
    private String name;
    private String iconUrl;
    private String achievementType;
    private LocalDateTime dateAchieved;

    // Premium only
    private String description;
    private Boolean isShared;
}