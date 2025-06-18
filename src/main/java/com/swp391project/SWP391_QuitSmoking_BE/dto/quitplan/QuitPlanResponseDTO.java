package com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PlanType;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitPlanResponseDTO {
    private Integer quitPlanId;
//    private String planTypeName; // Tên loại kế hoạch (dễ đọc hơn ID)
    private ReductionQuitPlanType reductionType;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDate goalDate;
    private int initialSmokingAmount;
    private BigDecimal pricePerPack;
    private int cigarettesPerPack;
    private QuitPlanStatus status;
}

    // có thể thêm các trường tính toán hoặc suy ra ở đây nếu cần cho hiển thị trên UI
    // Ví dụ: private BigDecimal estimatedSavings;
//     private int daysRemaining;
}