package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QuitPlanRequest {
    @NotNull(message = "ID thành viên không được để trống")
    private UUID memberId;

    @NotBlank(message = "ID loại kế hoạch không được để trống")
    @Size(max = 50, message = "ID loại kế hoạch không được vượt quá 50 ký tự")
    private String planTypeId;

    @NotNull(message = "Loại giảm dần không được để trống")
    private ReductionQuitPlanType reductionType;

    @NotNull(message = "Ngày bắt đầu kế hoạch không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu kế hoạch phải ở hiện tại hoặc tương lai")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày mục tiêu không được để trống")
    @FutureOrPresent(message = "Ngày mục tiêu phải ở hiện tại hoặc tương lai")
    private LocalDate goalDate;

    @Min(value = 0, message = "Số lượng thuốc ban đầu không thể là số âm")
    @Max(value = 500, message = "Số lượng thuốc ban đầu không thể vượt quá 500")
    private int initialSmokingAmount;

    private QuitPlanStatus status;
}
