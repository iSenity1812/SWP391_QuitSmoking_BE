package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import com.swp391project.SWP391_QuitSmoking_BE.enums.PaymentMethod;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionCreateRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @Size(max = 3, message = "Currency code must be 3 characters or less")
    private String currency = "VND";

    private PaymentMethod paymentMethod = PaymentMethod.VNPAY;

    @Size(max = 1000, message = "Description must be 1000 characters or less")
    private String description;

    // Subscription information
    private UUID subscriptionId;

    @Size(max = 50, message = "Subscription type must be 50 characters or less")
    private String subscriptionType;

    @Size(max = 20, message = "Subscription duration must be 20 characters or less")
    private String subscriptionDuration;

    // Client information
    @Size(max = 45, message = "Client IP must be 45 characters or less")
    private String clientIP;

    @Size(max = 500, message = "User agent must be 500 characters or less")
    private String userAgent;

    // Additional fields for specific payment methods
    @Size(max = 100, message = "Return URL must be 100 characters or less")
    private String returnUrl;

    @Size(max = 100, message = "Cancel URL must be 100 characters or less")
    private String cancelUrl;

    @Size(max = 50, message = "Order type must be 50 characters or less")
    private String orderType = "billpayment";

    @Size(max = 2, message = "Language must be 2 characters or less")
    private String language = "vn";
}