package com.swp391project.SWP391_QuitSmoking_BE.dto.coach;

import lombok.Data;

import java.util.UUID;

@Data
public class CoachProfile {
    private UUID userId;
    private String fullName;
    private String coachBio;
    private String email;
    private String profilePicture;
    private boolean isActive;
}
