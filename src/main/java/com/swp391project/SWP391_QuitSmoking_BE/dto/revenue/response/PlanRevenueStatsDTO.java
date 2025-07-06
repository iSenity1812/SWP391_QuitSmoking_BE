package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Builder
public class PlanRevenueStatsDTO {
    private Integer planId;
    private String planName;
    private String planDisplayName;
    private BigDecimal planPrice;
    private Integer durationDays;

    // Revenue stats
    private BigDecimal totalRevenue;
    private Long totalTransactions;
    private Long successTransactions;
    private Long activeSubscriptions;
    private Double revenuePercentage;
    private Double successRate;
}
