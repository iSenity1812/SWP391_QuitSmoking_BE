package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanAdminResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.SubscriptionAdminResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UserProfile {
    private UUID userId; // Unique identifier for the user
    private String username; // Username of the user
    private String email; // Email address of the user
    private String profilePicture; // URL of the user's avatar image
    private Role role;
    private boolean isActive; // Indicates if the user account is active
    private LocalDateTime createdAt; // Timestamp when the user was created
    private Integer streakCount; // Number of consecutive days the user has not smoked

    // Add these fields for detailed admin view
    private List<SubscriptionAdminResponseDTO> subscriptions;
    private List<QuitPlanAdminResponseDTO> quitPlans;
}
