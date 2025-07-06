package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response;


import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserRevenueStatsDTO {
    private UUID userId;
    private String username;
    private String email;
    private Role role;
    private String profilePicture;

    // Current subscription
    private String currentPlanName;
    private LocalDateTime currentSubscriptionEnd;
    private Boolean hasActiveSubscription;

    // Revenue stats
    private BigDecimal totalRevenue;
    private Long totalTransactions;
    private Long successTransactions;
    private LocalDateTime firstTransactionDate;
    private LocalDateTime lastTransactionDate;
}