package com.swp391project.SWP391_QuitSmoking_BE.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAchievementDTO {
    private Long memberAchievementId;
    private UUID memberId;
    private Long achievementId;
    private boolean isShared;
    private LocalDateTime dateAchieved;
    
    // Achievement details
    private String name;
    private String description;
    private String iconUrl;
    private String achievementType;
} 