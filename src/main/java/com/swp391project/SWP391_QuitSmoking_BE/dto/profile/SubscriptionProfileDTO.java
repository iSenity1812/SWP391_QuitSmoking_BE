package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionProfileDTO {
    private Long subscriptionId;
    private String packageName;
    private BigDecimal price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int daysRemaining;
    private String status;
}
