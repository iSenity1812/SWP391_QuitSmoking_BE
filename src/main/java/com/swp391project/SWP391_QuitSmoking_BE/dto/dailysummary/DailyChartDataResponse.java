package com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Mood;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyChartDataResponse {
    private LocalDate date;
    private int totalSmokedCount;
    private int totalCravingCount;
    private BigDecimal moneySaved; // Số tiền tiết kiệm được trong ngày đó
    private Mood mood;
}
