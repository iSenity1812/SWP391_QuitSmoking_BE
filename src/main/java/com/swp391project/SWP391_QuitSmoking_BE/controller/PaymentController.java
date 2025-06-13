package com.swp391project.SWP391_QuitSmoking_BE.controller;

import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentOrderRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentOrderResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.VnPayRequest;
import com.swp391project.SWP391_QuitSmoking_BE.service.PaymentService;
import com.swp391project.SWP391_QuitSmoking_BE.service.VnPayService;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@RequestBody PaymentOrderRequest request, HttpServletRequest httpRequest) {
        logger.info("Received payment order request for subscription: {}", request.getSubscriptionId());
        try {
            PaymentOrderResponse response = paymentService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating payment order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/vnpay/create")
    public Map<String, Object> createVnPayPayment(@RequestBody VnPayRequest request, HttpServletRequest httpRequest) {
        logger.info("Received VNPay payment creation request");
        Map<String, Object> response = new HashMap<>();
        try {
            String ip = httpRequest.getRemoteAddr();
            logger.info("Client IP: {}", ip);

            String paymentUrl = vnPayService.createPaymentUrl(request, ip);
            response.put("code", "00");
            response.put("message", "success");
            response.put("data", paymentUrl);

            logger.info("VNPay payment URL created successfully");
        } catch (Exception e) {
            logger.error("Error creating VNPay payment URL", e);
            response.put("code", "99");
            response.put("message", "Error: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<String> handleVnPayCallback(@RequestParam Map<String, String> fields) {
        logger.info("Received VNPay callback with parameters: {}", fields);
        try {
            // Verify signature
            boolean isValid = vnPayService.verifyVnpaySignature(fields);
            if (!isValid) {
                logger.error("Invalid VNPay signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid VNPAY signature.");
            }

            // Process payment
            paymentService.confirmVnPayCallback(fields);
            logger.info("VNPay callback processed successfully");

            // Return a user-friendly HTML page
            String htmlResponse = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "    <title>Payment Completed</title>" +
                    "    <style>" +
                    "        body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; }" +
                    "        .success { color: green; }" +
                    "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "    <div class='container'>" +
                    "        <h1 class='success'>Payment Processed Successfully!</h1>" +
                    "        <p>Your payment has been processed. You can now close this window and return to the application.</p>" +
                    "        <p>Transaction Reference: " + fields.get("vnp_TxnRef") + "</p>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";

            return ResponseEntity.ok()
                    .header("Content-Type", "text/html")
                    .body(htmlResponse);
        } catch (Exception e) {
            logger.error("Error processing VNPay callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing VNPAY callback: " + e.getMessage());
        }
    }
}