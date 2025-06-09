package com.swp391project.SWP391_QuitSmoking_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private Integer roleId;
    private String roleName;
    private Boolean isActive;
    private String profilePicture;
    private String notificationSetting;
}
