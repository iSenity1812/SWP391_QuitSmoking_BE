package com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan;

import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class QuitPlanRestartRequestDTO {
    @NotNull(message = "Ngày bắt đầu mới không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu không thể ở quá khứ") // Tùy thuộc vào nghiệp vụ, có thể cho phép ngày hiện tại
    private LocalDateTime newStartDate;

    @NotNull(message = "Ngày mục tiêu mới không được để trống")
    @FutureOrPresent(message = "Ngày mục tiêu phải ở hiện tại hoặc tương lai")
    private LocalDate newGoalDate;

    @Min(value = 0, message = "Số điếu ban đầu không thể là số âm")
    @NotNull(message = "Số điếu ban đầu không được để trống")
    private Integer newInitialSmokingAmount;

    @DecimalMin(value = "0.00", inclusive = true, message = "Giá mỗi gói thuốc không thể là số âm")
    @Digits(integer = 6, fraction = 2, message = "Giá mỗi gói thuốc tối đa 6 số nguyên, 2 số thập phân")
    @NotNull(message = "Giá mỗi gói thuốc không được để trống")
    private BigDecimal newPricePerPack;

    @Min(value = 1, message = "Số điếu mỗi gói phải lớn hơn 0")
    @NotNull(message = "Số điếu mỗi gói không được để trống")
    private Integer newCigarettesPerPack;

    private ReductionQuitPlanType newReductionType;
}
