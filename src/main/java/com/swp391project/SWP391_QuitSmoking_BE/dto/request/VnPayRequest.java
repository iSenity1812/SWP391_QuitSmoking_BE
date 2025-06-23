package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import lombok.Data;

@Data
public class VnPayRequest {
    private long amount; // Số tiền (cần là long)
    private String orderInfo;
    private String orderType;
    private String bankCode;
    private String language;
    private String billingFullname;
    private String billingEmail;
    private String billingMobile;
    private String transactionId; // Added field for tracking

    // Getters và setters được tự động tạo bởi Lombok @Data.
}