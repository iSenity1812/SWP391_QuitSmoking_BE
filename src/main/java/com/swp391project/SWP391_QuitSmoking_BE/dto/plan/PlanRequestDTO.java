package com.swp391project.SWP391_QuitSmoking_BE.dto.plan;

import com.swp391project.SWP391_QuitSmoking_BE.enums.PaidPlanType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanRequestDTO {
    @NotNull(message = "Loại gói không được để trống")
    private PaidPlanType planName;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự") // Giới hạn kích thước cho mô tả
    private String description;

    @NotNull(message = "Giá gói không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá gói phải không âm")
    @Digits(integer = 10, fraction = 2, message = "Giá gói có tối đa 10 chữ số phần nguyên và 2 chữ số phần thập phân")
    private BigDecimal price;

    @NotNull(message = "Giá trị thời hạn không được để trống")
    @Min(value = 1, message = "Giá trị thời hạn phải lớn hơn 0")
    private Integer durationValue; // Thời gian thực hiện kế hoạch (tính bằng ngày) (14, 30, 90)
}
