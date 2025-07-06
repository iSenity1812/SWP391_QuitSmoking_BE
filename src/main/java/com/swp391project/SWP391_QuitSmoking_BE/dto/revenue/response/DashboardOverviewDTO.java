package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardOverviewDTO {
    // Tổng quan doanh thu
    private BigDecimal totalRevenue;
    private BigDecimal totalRevenueSuccess;
    private BigDecimal totalRevenuePending;

    // Số lượng giao dịch
    private Long totalTransactions;
    private Long successTransactions;
    private Long pendingTransactions;
    private Long failedTransactions;
    private Long canceledTransactions;

    // Metrics
    private BigDecimal averageOrderValue;
    private Double successRate; // Tỷ lệ thành công
    private Long activeSubscriptions;
    private Long totalUsers;

    // So sánh với kỳ trước
    private BigDecimal revenueGrowthRate;
    private Double transactionGrowthRate;
}