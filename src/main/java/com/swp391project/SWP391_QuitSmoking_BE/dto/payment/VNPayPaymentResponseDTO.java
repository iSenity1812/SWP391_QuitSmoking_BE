package com.swp391project.SWP391_QuitSmoking_BE.dto.payment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VNPayPaymentResponseDTO {
    private String code; // Mã phản hồi (00: thành công, 99: lỗi không xác định, ...)
    private String message; // Thông báo
    private String paymentUrl; // URL để client redirect đến VNPay
}
