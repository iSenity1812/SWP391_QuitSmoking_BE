package com.swp391project.SWP391_QuitSmoking_BE.dto.coach;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CoachProfile {
    private UUID coachId;
    private String username;
    private String fullName;
    private String coachBio;
    private String profilePicture;
    private String specialties; // Chuỗi chứa các chuyên môn của Coach, ví dụ: "Chuyên môn 1, Chuyên môn 2"
    private BigDecimal rating;
    private boolean isActive;
}
