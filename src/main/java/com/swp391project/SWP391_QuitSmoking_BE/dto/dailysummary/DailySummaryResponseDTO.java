package com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Mood;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySummaryResponseDTO {
    private Integer dailySummaryId;
    private Integer quitPlanId; // ID của kế hoạch bỏ thuốc lá
    private int totalSmokedCount;
    private int totalCravingCount;
    private LocalDate trackDate;
    private Mood mood;
    private String note;
    private BigDecimal moneySaved;
    private boolean isCompliant; // Trường mới
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    private List<CravingTrackingResponseDTO> cravingTrackings;
}
