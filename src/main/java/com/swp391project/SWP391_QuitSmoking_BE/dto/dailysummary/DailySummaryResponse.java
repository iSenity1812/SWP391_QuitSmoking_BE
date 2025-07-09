package com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary;

import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Mood;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySummaryResponse {
    private Integer dailySummaryId;
    private Integer totalSmokedCount;
    private Integer totalCravingCount;
    private LocalDate trackDate;
    private Mood mood;
    private String note;
    private BigDecimal moneySaved;
    private boolean isGoalAchievedToday;
//    private List<CravingTrackingResponse> cravingTrackings;
}
