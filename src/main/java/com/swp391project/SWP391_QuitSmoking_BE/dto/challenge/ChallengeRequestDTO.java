package com.swp391project.SWP391_QuitSmoking_BE.dto.challenge;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeRequestDTO {
    @NotBlank(message = "Tên thử thách không được để trống")
    @Size(max = 100, message = "Tên thử thách không được quá 100 ký tự")
    private String challengeName;

    private String description; // Mô tả có thể null

    @FutureOrPresent(message = "Ngày bắt đầu phải là ngày hiện tại hoặc trong tương lai")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate; // Ngày kết thúc có thể null, validation sẽ được xử lý trong service

    @NotNull(message = "Giá trị mục tiêu không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị mục tiêu phải lớn hơn 0")
    private BigDecimal targetValue;

    @NotBlank(message = "Đơn vị không được để trống")
//  @Pattern(regexp = "cigarettes|USD|VND", message = "Đơn vị không hợp lệ. Chỉ chấp nhận 'cigarettes', 'USD', 'VND'.")
    private String unit;

}