package com.swp391project.SWP391_QuitSmoking_BE.dto;

import lombok.Data;

@Data
public class VnPayRequest {
    private int amount;
    private String orderInfo;
    private String orderType;
    private String bankCode;
    private String language;
    private String billingFullname;
    private String billingEmail;
    private String billingMobile;
    private String transactionId; // Added field for tracking
}