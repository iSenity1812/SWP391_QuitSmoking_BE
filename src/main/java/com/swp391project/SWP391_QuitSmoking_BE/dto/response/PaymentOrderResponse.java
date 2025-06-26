package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentOrderResponse {
    private UUID transactionId;
    private BigDecimal amount;
    private String status;
    private String subscriptionName;
    private String transactionMethod;
    private String paymentUrl;
    private String message;
}