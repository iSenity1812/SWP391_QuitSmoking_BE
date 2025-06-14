package com.swp391project.SWP391_QuitSmoking_BE.dto.premiumMember;

import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MemberSubscriptionResponse {
    private UUID memberSubscriptionId;
    private UUID memberId;
    private String subscriptionName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime purchasedAt;
}
