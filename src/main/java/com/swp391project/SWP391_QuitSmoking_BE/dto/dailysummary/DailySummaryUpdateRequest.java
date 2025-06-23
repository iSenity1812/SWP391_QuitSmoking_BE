package com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Mood;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class DailySummaryUpdateRequest {
    @NotNull(message = "ID nhật ký hàng ngày không được để trống")
    private Integer dailySummaryId;
    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    private Integer totalSmokedCount;
    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    private Integer totalCravingCount;
    private Double cravingSeverityAverage;
    private Double moodAverage;
    private Mood mood;
    private String note;
}
