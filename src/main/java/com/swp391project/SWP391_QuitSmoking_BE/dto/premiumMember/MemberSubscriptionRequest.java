package com.swp391project.SWP391_QuitSmoking_BE.dto.premiumMember;

import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MemberSubscriptionRequest {
    @NotNull(message = "Member ID không được để trống")
    private UUID memberId;
    @NotNull(message = "Subscription ID không được để trống")
    private Integer subscriptionId;
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate = LocalDateTime.now();
}
