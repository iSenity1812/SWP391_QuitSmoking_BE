package com.swp391project.SWP391_QuitSmoking_BE.dto.craving;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CravingTrackingResponseDTO {
    private Integer cravingTrackingId;
    private Integer dailySummaryId;
    private LocalDateTime trackTime;
    private Integer smokedCount;
    private Integer cravingsCount;
    private Situation situation;
    private WithWhom withWhom;
}
