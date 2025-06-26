package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class PaymentConfirmResponse {
    private UUID transactionId;
    private String status;
    private boolean success;
    private String message;
}