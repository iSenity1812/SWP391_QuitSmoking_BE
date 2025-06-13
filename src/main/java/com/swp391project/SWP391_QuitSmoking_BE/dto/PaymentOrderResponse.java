package com.swp391project.SWP391_QuitSmoking_BE.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class PaymentOrderResponse {
    private UUID transactionId;
    private BigDecimal amount;
    private String status;
    private String subscriptionName;
    private String transactionMethod;
    private String paymentUrl; // Added field for redirect
}