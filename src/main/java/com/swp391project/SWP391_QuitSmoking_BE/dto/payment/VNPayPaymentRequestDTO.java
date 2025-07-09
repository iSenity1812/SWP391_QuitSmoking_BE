package com.swp391project.SWP391_QuitSmoking_BE.dto.payment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VNPayPaymentRequestDTO {
    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "10000.0", inclusive = true, message = "Số tiền phải tối thiểu là 10.000 VND")
    @Min(value = 10000, message = "Số tiền phải lớn hơn hoặc bằng 10.000 VND")
    @Digits(integer = 10, fraction = 2, message = "Số tiền có tối đa 10 chữ số phần nguyên và 2 chữ số phần thập phân")
    private BigDecimal amount; // Số tiền thanh toán

    @NotBlank(message = "Thông tin đơn hàng không được để trống")
    @Size(max = 255, message = "Thông tin đơn hàng không được vượt quá 255 ký tự")
    private String orderInfo;

    private String orderType = "other"; // Loại đơn hàng, có thể để mặc định hoặc cho phép chọn
    private String bankCode; // Mã ngân hàng (optional)
    private Integer planId; // ID của gói Plan mà người dùng muốn mua

    @NotNull(message = "ID người dùng không được để trống")
    private UUID userId;

}
