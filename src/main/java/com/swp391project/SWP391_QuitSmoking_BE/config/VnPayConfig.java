package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@Validated
public class VnPayConfig {
    private static final Logger logger = LoggerFactory.getLogger(VnPayConfig.class);

    @NotBlank(message = "VNPay TMN Code cannot be blank")
    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @NotBlank(message = "VNPay Hash Secret cannot be blank")
    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @NotBlank(message = "VNPay Pay URL cannot be blank")
    @Value("${vnpay.pay-url}")
    private String payUrl;

    public static String returnUrl;

    @Value("${vnpay.timeout-minutes:15}")
    private int timeoutMinutes;

    @Value("${vnpay.max-amount:50000000}")
    private long maxAmount; // 50 million VND

    @Value("${vnpay.min-amount:10000}")
    private long minAmount; // 10,000 VND

    @PostConstruct
    public void init() {
        logger.info("VNPay configuration initialized");
        logger.info("VNPay TMN Code: {}", this.tmnCode);
        logger.info("VNPay URL: {}", this.payUrl);
        logger.info("VNPay Return URL: {}", this.returnUrl);
        logger.info("VNPay Timeout: {} minutes", this.timeoutMinutes);
        logger.info("VNPay Amount Range: {} - {}", this.minAmount, this.maxAmount);
    }

    // Getters
    public String getTmnCode() {
        return tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public int getTimeoutMinutes() {
        return timeoutMinutes;
    }

    public long getMaxAmount() {
        return maxAmount;
    }

    public long getMinAmount() {
        return minAmount;
    }
}
