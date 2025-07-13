package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitStatsProfileDTO {
    private Long daysWithoutSmoking;
    private Long cigarettesAvoided;
    private Long moneySaved;

    // Premium only fields
    private Integer totalSmokedSinceStart;
    private Integer totalCravingsSinceStart;
    private Double averageDailyCravings;
}