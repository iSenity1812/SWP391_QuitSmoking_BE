package com.swp391project.SWP391_QuitSmoking_BE.dto.payment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long transactionId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String description;
    private String paymentUrl;
    private String transactionReference;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}