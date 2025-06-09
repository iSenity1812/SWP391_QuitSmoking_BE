package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QuitPlanResponse {
    private String planTypeName;
    private ReductionQuitPlanType reductionType;
    private LocalDateTime startDate;
    private LocalDate goalDate;
    private int initialSmokingAmount;
    private QuitPlanStatus status;
}
