package com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PlanType;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitPlanResponseDTO {
    private Integer quitPlanId;
    private UUID memberId; // Có thể hiển thị ID của Member
    private PlanType planType; // Tên loại kế hoạch (ví dụ: "Gradual")
    private ReductionQuitPlanType reductionType;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDate goalDate;
    private int initialSmokingAmount;
    private QuitPlanStatus status;
}
