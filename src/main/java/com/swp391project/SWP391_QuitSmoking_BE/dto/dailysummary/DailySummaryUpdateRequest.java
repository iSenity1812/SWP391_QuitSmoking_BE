package com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Mood;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySummaryUpdateRequest {
    @NotNull(message = "ID nhật ký hàng ngày không được để trống")
    private Mood mood;
    private String note;
}
