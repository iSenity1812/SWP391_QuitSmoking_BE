package com.swp391project.SWP391_QuitSmoking_BE.dto.plan;

import com.swp391project.SWP391_QuitSmoking_BE.enums.PaidPlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanResponseDTO {
    private Integer planId;
    private PaidPlanType planName;
    private String description;
    private BigDecimal price;
    private Integer durationValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
