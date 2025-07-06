package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentMethodStatsDTO {
    private String paymentMethod;
    private Long totalTransactions;
    private Long successTransactions;
    private Long failedTransactions;
    private BigDecimal totalRevenue;
    private BigDecimal successRevenue;
    private Double successRate;
    private Double revenuePercentage;
}