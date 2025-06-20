package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PaymentAuditLog;
import com.swp391project.SWP391_QuitSmoking_BE.repository.PaymentAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentAuditService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentAuditService.class);

    @Autowired
    private PaymentAuditLogRepository auditLogRepository;

    @Transactional
    public void logPaymentAttempt(UUID transactionId, String action, String status,
                                  String details, String clientIP) {
        try {
            PaymentAuditLog auditLog = new PaymentAuditLog();
            auditLog.setTransactionId(transactionId);
            auditLog.setAction(action);
            auditLog.setStatus(status);
            auditLog.setDetails(details);
            auditLog.setClientIP(clientIP);
            auditLog.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            logger.error("Failed to log payment audit", e);
            // Don't throw exception to avoid affecting main payment flow
        }
    }

    @Transactional
    public void logVnPayCallback(String transactionId, Map<String, String> vnpayParams,
                                 String clientIP, boolean isValid) {
        try {
            String status = isValid ? "SUCCESS" : "FAILED";
            String details = String.format("VNPay callback - Response Code: %s, Amount: %s, Valid: %s",
                    vnpayParams.get("vnp_ResponseCode"),
                    vnpayParams.get("vnp_Amount"),
                    isValid);

            UUID txnId = null;
            try {
                txnId = UUID.fromString(transactionId);
            } catch (Exception e) {
                logger.warn("Invalid transaction ID format: {}", transactionId);
            }

            logPaymentAttempt(txnId, "VNPAY_CALLBACK", status, details, clientIP);
        } catch (Exception e) {
            logger.error("Failed to log VNPay callback audit", e);
        }
    }

    @Transactional
    public void logPaymentCreation(UUID transactionId, String memberID, String subscriptionID,
                                   String amount, String clientIP) {
        try {
            String details = String.format("Payment created - Member: %s, Subscription: %s, Amount: %s",
                    memberID, subscriptionID, amount);

            logPaymentAttempt(transactionId, "PAYMENT_CREATED", "PENDING", details, clientIP);
        } catch (Exception e) {
            logger.error("Failed to log payment creation audit", e);
        }
    }
}
