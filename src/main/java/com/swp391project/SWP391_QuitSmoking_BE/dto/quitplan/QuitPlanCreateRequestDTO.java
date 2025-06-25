package com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuitPlanCreateRequestDTO {
//    // Client gửi ID của member
//    @NotNull(message = "ID thành viên không được để trống")
//    private UUID memberId;

//    @NotNull(message = "Loại kế hoạch không được để trống")
//    private Integer planTypeId; // Hoặc String planTypeName nếu bạn truyền tên
    // Nếu là FK tới PlanType, nên dùng ID của PlanType.

    @NotNull(message = "Kiểu giảm dần không được để trống")
    private ReductionQuitPlanType reductionType;

//    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu không thể ở quá khứ")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate; // Người dùng nhập ngày bắt đầu

    @NotNull(message = "Ngày mục tiêu không được để trống")
    @FutureOrPresent(message = "Ngày mục tiêu phải ở tương lai")
    private LocalDate goalDate;

    @Min(value = 1, message = "Số lượng thuốc ban đầu phải lớn hơn 0")
    @Max(value = 500, message = "Số lượng thuốc ban đầu không thể vượt quá 500")
    @NotNull(message = "Số lượng thuốc ban đầu không được để trống")
    private int initialSmokingAmount;

    @NotNull(message = "Số điếu thuốc/gói không được để trống")
    @Min(value = 1, message = "Số điếu thuốc trong mỗi gói phải lớn hơn 0")
    private int cigarettesPerPack; // Số điếu thuốc trong mỗi gói

    @NotNull(message = "Số tiền/gói thuốc không được để trống")
    @DecimalMin(value = "0.00", message = "Số tiền không thể là số âm")
    @DecimalMax(value = "999999.99", inclusive = true, message = "Số tiền chỉ có thể tối đa 999.999VND") //6 số nguyên, 2 số thập phân
    private BigDecimal PricePerPack;
}
