package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.request.PaymentOrderRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.PaymentOrderResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentConfirmRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentConfirmResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest; // <-- THÊM IMPORT NÀY
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payment")
public class PaymentAPI {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@Valid @RequestBody PaymentOrderRequest request, HttpServletRequest httpRequest) { // <-- THÊM HttpServletRequest
        // Lấy IP của client, tương tự như cách bạn đã làm trong PaymentController
        String clientIP = getClientIP(httpRequest);
        PaymentOrderResponse response = paymentService.createOrder(request, clientIP); // <-- TRUYỀN clientIP VÀO ĐÂY
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request) {
        PaymentConfirmResponse response = paymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    // <-- THÊM PHƯƠNG THỨC getClientIP NÀY VÀO LỚP PaymentAPI
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }
}
