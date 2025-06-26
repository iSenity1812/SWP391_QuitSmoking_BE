package com.swp391project.SWP391_QuitSmoking_BE;

import com.swp391project.SWP391_QuitSmoking_BE.config.VnPayConfig;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.PaymentOrderRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.VnPayRequest;
import com.swp391project.SWP391_QuitSmoking_BE.service.PaymentService;
import com.swp391project.SWP391_QuitSmoking_BE.service.VnPayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentSystemTest {

    @Autowired
    private VnPayConfig vnPayConfig;

    @Autowired
    private VnPayService vnPayService;

    @Test
    public void testVnPayConfigurationLoaded() {
        // Test VnPay configuration is properly loaded
        assertNotNull(vnPayConfig.getTmnCode());
        assertNotNull(vnPayConfig.getHashSecret());
        assertNotNull(vnPayConfig.getPayUrl());
        assertNotNull(vnPayConfig.getReturnUrl());

        System.out.println("VnPay TMN Code: " + vnPayConfig.getTmnCode());
        System.out.println("VnPay Pay URL: " + vnPayConfig.getPayUrl());
        System.out.println("VnPay Return URL: " + vnPayConfig.getReturnUrl());

        assertTrue(vnPayConfig.getPayUrl().contains("sandbox"), "Should use sandbox URL");
    }

    @Test
    public void testVnPayPaymentUrlGeneration() {
        // Test VnPay payment URL generation
        VnPayRequest request = new VnPayRequest();
        request.setAmount(100000); // 100,000 VND
        request.setOrderInfo("Test payment for premium subscription");
        request.setTransactionId(UUID.randomUUID().toString());
        request.setOrderType("billpayment");
        request.setLanguage("vn");

        String paymentUrl = vnPayService.createPaymentUrl(request, "127.0.0.1");

        assertNotNull(paymentUrl);
        assertTrue(paymentUrl.startsWith("https://sandbox.vnpayment.vn"));
        assertTrue(paymentUrl.contains("vnp_Amount=10000000")); // Amount in VND cents
        assertTrue(paymentUrl.contains("vnp_TmnCode=" + vnPayConfig.getTmnCode()));
        assertTrue(paymentUrl.contains("vnp_SecureHash="));

        System.out.println("Generated Payment URL: " + paymentUrl);
    }

    @Test
    public void testPaymentAmountValidation() {
        VnPayRequest request = new VnPayRequest();
        request.setAmount(5000); // Below minimum
        request.setOrderInfo("Test payment");
        request.setTransactionId(UUID.randomUUID().toString());

        assertThrows(Exception.class, () -> {
            vnPayService.createPaymentUrl(request, "127.0.0.1");
        }, "Should throw exception for amount below minimum");
    }

    @Test
    public void testPaymentUrlContainsRequiredParameters() {
        VnPayRequest request = new VnPayRequest();
        request.setAmount(50000);
        request.setOrderInfo("Test subscription payment");
        request.setTransactionId(UUID.randomUUID().toString());
        request.setOrderType("billpayment");
        request.setLanguage("vn");

        String paymentUrl = vnPayService.createPaymentUrl(request, "192.168.1.1");

        // Check required VnPay parameters
        assertTrue(paymentUrl.contains("vnp_Version="));
        assertTrue(paymentUrl.contains("vnp_Command="));
        assertTrue(paymentUrl.contains("vnp_TmnCode="));
        assertTrue(paymentUrl.contains("vnp_Amount="));
        assertTrue(paymentUrl.contains("vnp_CurrCode=VND"));
        assertTrue(paymentUrl.contains("vnp_TxnRef="));
        assertTrue(paymentUrl.contains("vnp_OrderInfo="));
        assertTrue(paymentUrl.contains("vnp_OrderType="));
        assertTrue(paymentUrl.contains("vnp_Locale="));
        assertTrue(paymentUrl.contains("vnp_ReturnUrl="));
        assertTrue(paymentUrl.contains("vnp_IpAddr="));
        assertTrue(paymentUrl.contains("vnp_CreateDate="));
        assertTrue(paymentUrl.contains("vnp_ExpireDate="));
        assertTrue(paymentUrl.contains("vnp_SecureHash="));

        System.out.println("✅ Payment URL contains all required VnPay parameters");
    }
}