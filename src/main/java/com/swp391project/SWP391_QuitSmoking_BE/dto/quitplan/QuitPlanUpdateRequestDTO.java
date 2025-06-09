package com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan;


import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitPlanUpdateRequestDTO {
    @NotNull(message = "ID kế hoạch không được để trống")
    private Long quitPlanId; // Bắt buộc cho cập nhật

    // Các trường dưới đây có thể @Nullable nếu cập nhật một phần (PATCH)
    private String planTypeId;
    private ReductionQuitPlanType reductionType;
    private LocalDateTime startDate;
    private LocalDate goalDate;
    private Integer initialSmokingAmount; // Dùng Integer để có thể là null
    private QuitPlanStatus status; // Có thể cho phép cập nhật trạng thái
}
