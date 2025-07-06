package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionDetailDTO {
    private UUID transactionId;
    private String externalTransactionId;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private TransactionStatus status;
    private String paymentMethod;

    // User info
    private UUID userId;
    private String username;
    private String userEmail;
    private Role userRole;
    private String userProfilePicture;

    // Plan info
    private Integer planId;
    private String planName;
    private String planDisplayName;
    private BigDecimal planPrice;

    // Subscription info (nếu có)
    private Long subscriptionId;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private Boolean isSubscriptionActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}