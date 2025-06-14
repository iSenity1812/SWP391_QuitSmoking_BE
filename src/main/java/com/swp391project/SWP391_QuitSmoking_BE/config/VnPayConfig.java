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

    // LƯU Ý: Đảm bảo rằng bạn đã cấu hình vnpay.return-url trong application.properties
    // Hoặc nó sẽ được cập nhật động bằng updateReturnUrl()
    @NotBlank(message = "VNPay Return URL cannot be blank")
    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.timeout-minutes:15}")
    private int timeoutMinutes;

    @Value("${vnpay.max-amount:50000000}")
    private long maxAmount; // 50 million VND

    @Value("${vnpay.min-amount:10000}")
    private long minAmount; // 10,000 VND

    // Static fields for backward compatibility
    // CÁC TRƯỜNG STATIC NÀY CÓ THỂ LÀ NGUYÊN NHÂN GÂY RẮC RỐI VÌ NÓ KHÔNG PHẢI LÀ SPRING BEAN PROPERTY
    // NẾU BẠN KHÔNG SỬ DỤNG CÁC TRƯỜNG STATIC NÀY ĐỂ TRUY CẬP CẤU HÌNH Ở BẤT KỲ ĐÂU NGOÀI VnPayConfig,
    // BẠN CÓ THỂ CÂN NHẮC XÓA CHÚNG ĐỂ ĐƠN GIẢN HÓA VÀ TRÁNH NHẦM LẪN.
    // Nếu có sử dụng, hãy chắc chắn rằng bạn truy cập chúng SAU khi @PostConstruct được gọi.
    public static String vnp_TmnCode;
    public static String vnp_HashSecret;
    public static String vnp_PayUrl;
    public static String vnp_ReturnUrl;

    @PostConstruct
    public void init() {
        vnp_TmnCode = this.tmnCode;
        vnp_HashSecret = this.hashSecret;
        vnp_PayUrl = this.payUrl;
        vnp_ReturnUrl = this.returnUrl;

        logger.info("VNPay configuration initialized for environment");
        logger.info("VNPay TMN Code: {}", this.tmnCode); // Thêm log để kiểm tra giá trị thực tế
        logger.info("VNPay URL: {}", this.payUrl);
        logger.info("VNPay Return URL: {}", this.returnUrl);
        logger.info("VNPay Timeout: {} minutes", this.timeoutMinutes);
    }

    // Getters
    public String getTmnCode() { return tmnCode; }
    public String getHashSecret() { return hashSecret; }
    public String getPayUrl() { return payUrl; }
    public String getReturnUrl() { return returnUrl; }
    public int getTimeoutMinutes() { return timeoutMinutes; }
    public long getMaxAmount() { return maxAmount; }
    public long getMinAmount() { return minAmount; }

    // Method to update return URL based on environment (for backward compatibility)
    public static void updateReturnUrl(String baseUrl) {
        vnp_ReturnUrl = baseUrl + "/api/payment/vnpay/callback";
    }
}
