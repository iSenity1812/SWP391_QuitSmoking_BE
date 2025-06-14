package com.swp391project.SWP391_QuitSmoking_BE.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private UUID userId;
    private String username;
    private String email;
    private String roleName;
    private boolean isActive;
    private String message;

    public LoginResponse(String token, UUID userId, String username, String email, String roleName, boolean isActive) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.isActive = isActive;
        this.message = "Login successful";
    }

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
