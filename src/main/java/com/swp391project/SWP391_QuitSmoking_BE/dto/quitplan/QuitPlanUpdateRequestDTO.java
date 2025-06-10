package com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan;


import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitPlanUpdateRequestDTO {
    @NotNull(message = "ID kế hoạch không được để trống")
    private Integer quitPlanId; // Bắt buộc cho cập nhật

    // Các trường dưới đây có thể @Nullable nếu cập nhật một phần (PATCH)
    private String planTypeId;
    @DecimalMin(value = "0.00", inclusive = true, message = "Số tiền không thể là số âm")
    //6 số nguyên, 2 số thập phân
    @DecimalMax(value = "999999.99", inclusive = true, message = "Số tiền chỉ có thể tối đa 999.999VND")
    private BigDecimal PricePerPack;
}
