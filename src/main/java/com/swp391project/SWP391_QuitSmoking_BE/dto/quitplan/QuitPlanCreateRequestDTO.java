package com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan;

import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitPlanCreateRequestDTO {
    @NotNull(message = "Loại kế hoạch không được để trống")
    private String planTypeId; // Hoặc String planTypeName nếu bạn truyền tên
    // Nếu là FK tới PlanType, nên dùng ID của PlanType.

    @NotNull(message = "Kiểu giảm dần không được để trống")
    private ReductionQuitPlanType reductionType;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu không thể ở quá khứ")
    private LocalDateTime startDate; // Người dùng nhập ngày bắt đầu

    @NotNull(message = "Ngày mục tiêu không được để trống")
    @Future(message = "Ngày mục tiêu phải ở tương lai")
    private LocalDate goalDate;

    @Min(value = 1, message = "Số lượng thuốc ban đầu phải lớn hơn 0")
    @NotNull(message = "Số lượng thuốc ban đầu không được để trống")
    private int initialSmokingAmount;
}
