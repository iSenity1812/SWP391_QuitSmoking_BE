package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PaymentAuditLog;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.service.PaymentAuditLogService;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.PaymentOrderRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.PaymentOrderResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.PaymentConfirmRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.PaymentConfirmResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.PaymentService;
import com.swp391project.SWP391_QuitSmoking_BE.service.VnPayService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@SecurityRequirement(name = "payment_api")
public class PaymentAPI {
    @Autowired
    private PaymentAuditLogService paymentAuditLogService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VnPayService vnPayService;

    @GetMapping("/audit-logs")
    public List<PaymentAuditLog> getAllPaymentAuditLogs() {
        return paymentAuditLogService.getAllPaymentAuditLogs();
    }

    @GetMapping("/audit-logs/{id}")
    public ResponseEntity<PaymentAuditLog> getPaymentAuditLogById(@PathVariable Integer id) {
        return paymentAuditLogService.getPaymentAuditLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/audit-logs")
    public PaymentAuditLog createPaymentAuditLog(@RequestBody PaymentAuditLog paymentAuditLog) {
        return paymentAuditLogService.createPaymentAuditLog(paymentAuditLog);
    }

    @PutMapping("/audit-logs/{id}")
    public ResponseEntity<PaymentAuditLog> updatePaymentAuditLog(@PathVariable Integer id,
            @RequestBody PaymentAuditLog paymentAuditLogDetails) {
        try {
            PaymentAuditLog updatedPaymentAuditLog = paymentAuditLogService.updatePaymentAuditLog(id,
                    paymentAuditLogDetails);
            return ResponseEntity.ok(updatedPaymentAuditLog);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/audit-logs/{id}")
    public ResponseEntity<Void> deletePaymentAuditLog(@PathVariable Integer id) {
        try {
            paymentAuditLogService.deletePaymentAuditLog(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create-order")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<?> createPaymentOrder(
            @RequestBody PaymentOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {
        try {
            // Get authenticated user ID
            User authenticatedUser = (User) userDetails;
            String authenticatedMemberId = authenticatedUser.getUserId().toString();
            
            // Override memberId from request with authenticated user ID for security
            request.setMemberId(authenticatedMemberId);
            
            String clientIP = getClientIP(httpRequest);
            PaymentOrderResponse response = paymentService.createOrder(request, clientIP);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Trả về thông tin lỗi chi tiết
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Payment creation failed",
                    "message", e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now().toString()));
        }
    }

    @PostMapping("/vnpay/callback")
    public ResponseEntity<String> vnPayCallback(
            @RequestParam Map<String, String> queryParams,
            HttpServletRequest httpRequest) {
        try {
            String clientIP = getClientIP(httpRequest);
            paymentService.confirmVnPayCallback(queryParams, clientIP);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing callback");
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @RequestBody PaymentConfirmRequest request) {
        try {
            PaymentConfirmResponse response = paymentService.confirmPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }
        return request.getRemoteAddr();
    }
}