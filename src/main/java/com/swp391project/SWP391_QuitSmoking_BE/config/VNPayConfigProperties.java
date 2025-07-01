package com.swp391project.SWP391_QuitSmoking_BE.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfigProperties {
    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.apiUrl}")
    private String apiUrl;

    @Value("${vnpay.ipnUrl}")
    private String ipnUrl;

    // Sử dụng @PostConstruct để gán các giá trị đã được inject vào các biến static của VNPayConfig
    @PostConstruct
    public void init() {
        VNPayConfig.vnp_PayUrl = this.payUrl;
        VNPayConfig.vnp_Returnurl = this.returnUrl;
        VNPayConfig.vnp_TmnCode = this.tmnCode;
        VNPayConfig.vnp_HashSecret = this.hashSecret;
        VNPayConfig.vnp_apiUrl = this.apiUrl;
        VNPayConfig.vnp_ipnUrl = this.ipnUrl;
    }
}
