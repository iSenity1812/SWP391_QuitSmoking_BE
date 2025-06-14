package com.swp391project.SWP391_QuitSmoking_BE.controller;

import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentOrderRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentOrderResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.VnPayRequest;
import com.swp391project.SWP391_QuitSmoking_BE.exception.PaymentProcessingException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.VnPayException;
import com.swp391project.SWP391_QuitSmoking_BE.service.PaymentService;
import com.swp391project.SWP391_QuitSmoking_BE.service.VnPayService;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentOrderRequest request, HttpServletRequest httpRequest) {
        logger.info("Received payment order request for subscription: {}", request.getSubscriptionId());

        try {
            String clientIP = getClientIP(httpRequest);
            PaymentOrderResponse response = paymentService.createOrder(request, clientIP);
            return ResponseEntity.ok(response);
        } catch (PaymentProcessingException e) {
            logger.error("Payment processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage(), e.getErrorCode()));
        } catch (VnPayException e) {
            logger.error("VNPay error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(createErrorResponse("Payment service temporarily unavailable", e.getErrorCode()));
        } catch (Exception e) {
            logger.error("Unexpected error creating payment order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", "INTERNAL_ERROR"));
        }
    }

    @PostMapping("/vnpay/create")
    public ResponseEntity<Map<String, Object>> createVnPayPayment(@RequestBody VnPayRequest request, HttpServletRequest httpRequest) {
        logger.info("Received VNPay payment creation request");
        Map<String, Object> response = new HashMap<>();

        try {
            String ip = getClientIP(httpRequest);
            logger.info("Client IP: {}", ip);

            String paymentUrl = vnPayService.createPaymentUrl(request, ip);
            response.put("code", "00");
            response.put("message", "success");
            response.put("data", paymentUrl);

            logger.info("VNPay payment URL created successfully");
            return ResponseEntity.ok(response);
        } catch (VnPayException e) {
            logger.error("VNPay error creating payment URL: {}", e.getMessage());
            response.put("code", "99");
            response.put("message", "VNPay Error: " + e.getMessage());
            response.put("errorCode", e.getErrorCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error creating VNPay payment URL", e);
            response.put("code", "99");
            response.put("message", "System Error: " + e.getMessage());
            response.put("errorCode", "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<String> handleVnPayCallback(@RequestParam Map<String, String> fields, HttpServletRequest httpRequest) {
        logger.info("Received VNPay callback with parameters: {}", fields.keySet());

        try {
            String clientIP = getClientIP(httpRequest);

            // Process payment
            paymentService.confirmVnPayCallback(fields, clientIP);
            logger.info("VNPay callback processed successfully");

            // Return a user-friendly HTML page
            String htmlResponse = generateSuccessHtml(fields.get("vnp_TxnRef"));
            return ResponseEntity.ok()
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(htmlResponse);

        } catch (PaymentProcessingException e) {
            logger.error("Payment processing error in callback: {}", e.getMessage());
            String htmlResponse = generateErrorHtml("Payment processing failed: " + e.getMessage(), e.getErrorCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(htmlResponse);
        } catch (Exception e) {
            logger.error("Unexpected error processing VNPay callback", e);
            String htmlResponse = generateErrorHtml("System error occurred", "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(htmlResponse);
        }
    }

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

    private Map<String, Object> createErrorResponse(String message, String errorCode) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("errorCode", errorCode);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }

    private String generateSuccessHtml(String transactionRef) {
        return "<!DOCTYPE html>" +
                "<html lang='vi'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Thanh toán thành công</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; background-color: #f5f5f5; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                "        .success { color: #28a745; font-size: 24px; margin-bottom: 20px; }" +
                "        .info { color: #6c757d; margin: 10px 0; }" +
                "        .btn { background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 20px; }" +
                "        .icon { font-size: 48px; color: #28a745; margin-bottom: 20px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='icon'>✓</div>" +
                "        <h1 class='success'>Thanh toán thành công!</h1>" +
                "        <p class='info'>Giao dịch của bạn đã được xử lý thành công.</p>" +
                "        <p class='info'>Mã giao dịch: <strong>" + (transactionRef != null ? transactionRef : "N/A") + "</strong></p>" +
                "        <p class='info'>Bạn có thể đóng cửa sổ này và quay lại ứng dụng.</p>" +
                "        <a href='#' onclick='window.close()' class='btn'>Đóng cửa sổ</a>" +
                "    </div>" +
                "    <script>" +
                "        setTimeout(function() { window.close(); }, 5000);" +
                "    </script>" +
                "</body>" +
                "</html>";
    }

    private String generateErrorHtml(String errorMessage, String errorCode) {
        return "<!DOCTYPE html>" +
                "<html lang='vi'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Lỗi thanh toán</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; background-color: #f5f5f5; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                "        .error { color: #dc3545; font-size: 24px; margin-bottom: 20px; }" +
                "        .info { color: #6c757d; margin: 10px 0; }" +
                "        .btn { background-color: #dc3545; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 20px; }" +
                "        .icon { font-size: 48px; color: #dc3545; margin-bottom: 20px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='icon'>✗</div>" +
                "        <h1 class='error'>Thanh toán thất bại!</h1>" +
                "        <p class='info'>" + errorMessage + "</p>" +
                "        <p class='info'>Mã lỗi: <strong>" + errorCode + "</strong></p>" +
                "        <p class='info'>Vui lòng thử lại hoặc liên hệ hỗ trợ.</p>" +
                "        <a href='#' onclick='window.close()' class='btn'>Đóng cửa sổ</a>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
