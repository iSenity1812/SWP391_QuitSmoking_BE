package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import java.util.UUID;

public class PublicUserProfileDTO {
    private UUID userId;
    private String username;
    private String email;
    private String profilePicture;
    private String createdAt;

    public PublicUserProfileDTO(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profilePicture = user.getProfilePicture();
        this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;
    }

    public UUID getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfilePicture() { return profilePicture; }
    public String getCreatedAt() { return createdAt; }
} 