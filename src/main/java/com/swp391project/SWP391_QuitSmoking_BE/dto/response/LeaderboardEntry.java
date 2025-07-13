package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntry {
    private UUID memberId;
    private String username;
    private String profilePicture;
    private BigDecimal score;
    private int rank;
    private String achievementType; // MONEY_SAVED, DAYS_QUIT, CIGARETTES_AVOIDED
    private String displayValue; // Giá trị hiển thị (VD: "1,000,000 VND", "30 ngày")
} 