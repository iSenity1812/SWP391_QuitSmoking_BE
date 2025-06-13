package com.swp391project.SWP391_QuitSmoking_BE.config;

public class VnPayConfig {
    public static String vnp_TmnCode = "ZA6FG78P";
    public static String vnp_HashSecret = "Z25RB7TCSI1YRL6BXEZSORFP042ZQ8V8";
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    // This will be updated programmatically based on environment
    public static String vnp_ReturnUrl;

    // Method to update return URL based on environment
    public static void updateReturnUrl(String baseUrl) {
        vnp_ReturnUrl = baseUrl + "/api/payment/vnpay/callback";
    }
}