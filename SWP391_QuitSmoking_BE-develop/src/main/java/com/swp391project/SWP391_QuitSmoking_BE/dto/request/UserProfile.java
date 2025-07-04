package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class UserProfile {
    private UUID userId; // Unique identifier for the user
    private String username; // Username of the user
    private String email; // Email address of the user
    private String profilePicture; // URL of the user's avatar image
    private Role role;
    private boolean isActive; // Indicates if the user account is active
}
