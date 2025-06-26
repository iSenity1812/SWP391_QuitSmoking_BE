package com.swp391project.SWP391_QuitSmoking_BE.service;

// Các imports cho Request/Response DTO
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.PaymentOrderRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.PaymentOrderResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.PaymentConfirmRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.PaymentConfirmResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.VnPayRequest;

// Các imports khác đã có
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.exception.PaymentProcessingException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.VnPayException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private VnPayService vnPayService;
    @Autowired
    private TransactionMethodRepository transactionMethodRepository;
    @Autowired
    private PaymentAuditLogService paymentAuditLogService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(value = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public PaymentOrderResponse createOrder(PaymentOrderRequest request, String clientIP) {
        logger.info("Creating payment order for subscription ID: {}, member ID: {}",
                request.getSubscriptionId(), request.getMemberId());

        try {
            validatePaymentOrderRequest(request);

            Transaction transaction = new Transaction();
            UUID transactionId = UUID.randomUUID();
            transaction.setTransactionId(transactionId);
            transaction.setUserId(request.getMemberId());
            transaction.setAmount(java.math.BigDecimal.valueOf(request.getAmount()));
            transaction.setCurrency("VND");
            transaction.setStatus("PENDING");
            transaction.setPaymentMethod("VNPAY");
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setDescription(request.getOrderInfo());

            transactionRepository.save(transaction);
            logger.info("Created transaction with ID: {}", transactionId);

            VnPayRequest vnPayRequest = new VnPayRequest();
            vnPayRequest.setAmount(Math.round(request.getAmount()));
            vnPayRequest
                    .setOrderInfo(request.getOrderInfo() != null ? request.getOrderInfo() : "Thanh toan goi premium");
            vnPayRequest.setTransactionId(transactionId.toString());
            vnPayRequest.setOrderType("billpayment");
            vnPayRequest.setLanguage("vn");

            String paymentUrl = vnPayService.createPaymentUrl(vnPayRequest, clientIP);

            PaymentOrderResponse response = new PaymentOrderResponse();
            response.setTransactionId(transactionId);
            response.setAmount(java.math.BigDecimal.valueOf(request.getAmount()));
            response.setStatus("PENDING");
            response.setSubscriptionName("Premium Subscription");
            response.setTransactionMethod("VNPAY");
            response.setPaymentUrl(paymentUrl);

            logger.info("Payment order created successfully with payment URL");
            return response;

        } catch (VnPayException e) {
            logger.error("VNPay error creating payment order", e);
            throw new PaymentProcessingException("VNPay service error: " + e.getMessage(), e.getErrorCode(), e);
        } catch (PaymentProcessingException e) {
            logger.error("Payment processing error", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating payment order", e);
            throw new PaymentProcessingException("Failed to create payment order: " + e.getMessage(),
                    "UNEXPECTED_ERROR", e);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void confirmVnPayCallback(Map<String, String> fields, String clientIP) {
        logger.info("Processing VNPay callback with fields: {}", fields.keySet());

        try {
            String txnRef = fields.get("vnp_TxnRef");
            if (txnRef == null || txnRef.isEmpty()) {
                logger.error("Missing transaction reference in VNPay callback");
                throw new PaymentProcessingException("Missing transaction reference", "MISSING_TXN_REF");
            }

            UUID transactionId;
            try {
                transactionId = UUID.fromString(txnRef);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid transaction ID format: {}", txnRef);
                throw new PaymentProcessingException("Invalid transaction ID format", "INVALID_TXN_ID");
            }

            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> {
                        logger.error("Transaction not found with ID: {}", transactionId);
                        return new PaymentProcessingException("Transaction not found", "TRANSACTION_NOT_FOUND");
                    });

            if ("SUCCESS".equals(transaction.getStatus())) {
                logger.info("Transaction {} already marked as successful, skipping", transactionId);
                return;
            }

            if (!vnPayService.verifyVnpaySignature(fields)) {
                logger.error("Invalid VNPay signature for transaction: {}", transactionId);
                transaction.setStatus("FAILED");
                transactionRepository.save(transaction);
                throw new PaymentProcessingException("Invalid VNPay signature", "INVALID_SIGNATURE");
            }

            String responseCode = fields.get("vnp_ResponseCode");
            logger.info("VNPay response code: {} for transaction: {}", responseCode, transactionId);

            if ("00".equals(responseCode)) {
                processSuccessfulPayment(transaction, fields);
                logger.info("Payment successful for transaction: {}", transactionId);
            } else {
                processFailedPayment(transaction, responseCode);
                logger.warn("Payment failed for transaction: {}, response code: {}", transactionId, responseCode);
            }

        } catch (PaymentProcessingException e) {
            logger.error("Payment processing error in callback", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing VNPay callback", e);
            throw new PaymentProcessingException("Failed to process callback: " + e.getMessage(), "UNEXPECTED_ERROR",
                    e);
        }
    }

    private void processSuccessfulPayment(Transaction transaction, Map<String, String> fields) {
        transaction.setStatus("SUCCESS");
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Log successful payment
        PaymentAuditLog auditLog = new PaymentAuditLog();
        auditLog.setTransactionId(transaction.getTransactionId());
        auditLog.setEventType("PAYMENT_SUCCESS");
        auditLog.setStatus("SUCCESS");
        auditLog.setDetails("Payment completed successfully via VNPay");
        auditLog.setCreatedAt(LocalDateTime.now());
        paymentAuditLogService.createPaymentAuditLog(auditLog);
    }

    private void processFailedPayment(Transaction transaction, String responseCode) {
        transaction.setStatus("FAILED");
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Log failed payment
        PaymentAuditLog auditLog = new PaymentAuditLog();
        auditLog.setTransactionId(transaction.getTransactionId());
        auditLog.setEventType("PAYMENT_FAILED");
        auditLog.setStatus("FAILED");
        auditLog.setDetails("Payment failed with response code: " + responseCode);
        auditLog.setCreatedAt(LocalDateTime.now());
        paymentAuditLogService.createPaymentAuditLog(auditLog);
    }

    private void validatePaymentOrderRequest(PaymentOrderRequest request) {
        if (request.getAmount() <= 0) {
            throw new PaymentProcessingException("Invalid amount", "INVALID_AMOUNT");
        }
        if (request.getMemberId() == null) {
            throw new PaymentProcessingException("Member ID is required", "MISSING_MEMBER_ID");
        }
        if (request.getSubscriptionId() == null) {
            throw new PaymentProcessingException("Subscription ID is required", "MISSING_SUBSCRIPTION_ID");
        }
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        logger.info("Confirming payment for transaction: {}", request.getTransactionId());

        try {
            Transaction transaction = transactionRepository.findById(request.getTransactionId())
                    .orElseThrow(
                            () -> new PaymentProcessingException("Transaction not found", "TRANSACTION_NOT_FOUND"));

            PaymentConfirmResponse response = new PaymentConfirmResponse();
            response.setTransactionId(transaction.getTransactionId());
            response.setStatus(transaction.getStatus());
            response.setSuccess("SUCCESS".equals(transaction.getStatus()));
            response.setMessage("Payment " + (response.isSuccess() ? "confirmed successfully" : "failed"));

            return response;

        } catch (PaymentProcessingException e) {
            logger.error("Error confirming payment", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error confirming payment", e);
            throw new PaymentProcessingException("Failed to confirm payment: " + e.getMessage(), "UNEXPECTED_ERROR", e);
        }
    }
}