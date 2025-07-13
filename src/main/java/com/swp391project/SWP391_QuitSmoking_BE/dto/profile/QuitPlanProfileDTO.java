package com.swp391project.SWP391_QuitSmoking_BE.dto.profile;


import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitPlanProfileDTO {
    private Integer quitPlanId;
    private QuitPlanStatus status;
    private LocalDateTime startDate;
    private LocalDate goalDate;
    private Integer daysInPlan;
    private Integer daysToGoal;
    private Double progressPercentage;
    private ReductionQuitPlanType reductionType;

    // Premium only fields
    private Integer initialSmokingAmount;
    private Integer cigarettesPerPack;
    private BigDecimal pricePerPack;
    private BigDecimal pricePerCigarette;
}