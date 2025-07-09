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
//    @NotNull(message = "ID nhật ký hàng ngày không được để trống")
//    private Integer dailySummaryId;

    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    private Integer updateSmokedCount;
    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    private Integer updateCravingCount;
    private Mood mood;
    private String note;
}
