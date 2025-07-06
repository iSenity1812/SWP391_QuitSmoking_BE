package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RevenueByPeriodDTO {
    private String period; // "2024-01-01", "2024-W01", "2024-01"
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private BigDecimal revenue;
    private Long transactionCount;
    private Long successCount;
    private Long failedCount;
    private Double successRate;
}