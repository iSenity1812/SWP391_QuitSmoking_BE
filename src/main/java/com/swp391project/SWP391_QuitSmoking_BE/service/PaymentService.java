package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.exception.PaymentProcessingException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.VnPayException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role; // <-- THÊM IMPORT NÀY CHO ENUM ROLE

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
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository; // Đây là RoleRepository cho entity.Role
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VnPayService vnPayService;
    @Autowired
    private TransactionMethodRepository transactionMethodRepository;
    @Autowired
    private VnPaySecurityService securityService;
    @Autowired
    private PaymentAuditService auditService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public PaymentOrderResponse createOrder(PaymentOrderRequest request, String clientIP) {
        logger.info("Creating payment order for subscription ID: {}, member ID: {}",
                request.getSubscriptionId(), request.getMemberId());

        try {
            // Validate request
            validatePaymentOrderRequest(request);

            Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                    .orElseThrow(() -> {
                        logger.error("Subscription not found with ID: {}", request.getSubscriptionId());
                        return new PaymentProcessingException("Subscription not found", "SUBSCRIPTION_NOT_FOUND");
                    });

            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> {
                        logger.error("Member not found with ID: {}", request.getMemberId());
                        return new PaymentProcessingException("Member not found", "MEMBER_NOT_FOUND");
                    });

            TransactionMethod vnPayMethod = transactionMethodRepository.findByMethodName("VNPAY")
                    .orElseThrow(() -> {
                        logger.error("Transaction method VNPAY not found");
                        return new PaymentProcessingException("Transaction method VNPAY not found", "METHOD_NOT_FOUND");
                    });

            // Create transaction record
            Transaction transaction = new Transaction();
            UUID transactionId = UUID.randomUUID();
            transaction.setTransactionId(transactionId);
            transaction.setAmount(subscription.getPrice());
            transaction.setMember(member);
            transaction.setTransactionMethod(vnPayMethod);
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setTransactionDate(LocalDateTime.now());

            transactionRepository.save(transaction);
            logger.info("Created transaction with ID: {}", transactionId);

            // Log audit
            auditService.logPaymentCreation(transactionId, member.getMemberId().toString(),
                    subscription.getSubscriptionId().toString(), subscription.getPrice().toString(), clientIP);

            // Create VNPay request
            VnPayRequest vnPayRequest = new VnPayRequest();
            vnPayRequest.setAmount(subscription.getPrice().intValue());
            vnPayRequest.setOrderInfo("Thanh toan goi: " + subscription.getName());
            vnPayRequest.setTransactionId(transactionId.toString());
            vnPayRequest.setOrderType("billpayment");
            vnPayRequest.setLanguage("vn");

            // Generate payment URL
            String paymentUrl = vnPayService.createPaymentUrl(vnPayRequest, clientIP);

            // Create response
            PaymentOrderResponse response = new PaymentOrderResponse();
            response.setTransactionId(transactionId);
            response.setAmount(subscription.getPrice());
            response.setStatus("PENDING");
            response.setSubscriptionName(subscription.getName());
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
            throw new PaymentProcessingException("Failed to create payment order: " + e.getMessage(), "UNEXPECTED_ERROR", e);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void confirmVnPayCallback(Map<String, String> fields, String clientIP) {
        logger.info("Processing VNPay callback with fields: {}", fields.keySet());

        try {
            // Extract transaction ID from VNPay response
            String txnRef = fields.get("vnp_TxnRef");
            if (txnRef == null || txnRef.isEmpty()) {
                logger.error("Missing transaction reference in VNPay callback");
                throw new PaymentProcessingException("Missing transaction reference", "MISSING_TXN_REF");
            }

            // Security validation
            if (!securityService.isValidCallbackRequest(clientIP, txnRef)) {
                logger.error("Security validation failed for callback from IP: {}", clientIP);
                auditService.logVnPayCallback(txnRef, fields, clientIP, false);
                throw new PaymentProcessingException("Security validation failed", "SECURITY_VALIDATION_FAILED");
            }

            UUID transactionId;
            try {
                transactionId = UUID.fromString(txnRef);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid transaction ID format: {}", txnRef);
                auditService.logVnPayCallback(txnRef, fields, clientIP, false);
                throw new PaymentProcessingException("Invalid transaction ID format", "INVALID_TXN_ID");
            }

            // Find transaction with pessimistic lock
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> {
                        logger.error("Transaction not found with ID: {}", transactionId);
                        auditService.logVnPayCallback(txnRef, fields, clientIP, false);
                        return new PaymentProcessingException("Transaction not found", "TRANSACTION_NOT_FOUND");
                    });

            // Check if already processed (idempotency)
            if (transaction.getStatus() == TransactionStatus.SUCCESS) {
                logger.info("Transaction {} already marked as successful, skipping", transactionId);
                securityService.markTransactionAsProcessed(txnRef);
                return;
            }

            // Verify signature
            if (!vnPayService.verifyVnpaySignature(fields)) {
                logger.error("Invalid VNPay signature for transaction: {}", transactionId);
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                auditService.logVnPayCallback(txnRef, fields, clientIP, false);
                throw new PaymentProcessingException("Invalid VNPay signature", "INVALID_SIGNATURE");
            }

            // Process response code
            String responseCode = fields.get("vnp_ResponseCode");
            logger.info("VNPay response code: {} for transaction: {}", responseCode, transactionId);

            if ("00".equals(responseCode)) {
                // Success case
                processSuccessfulPayment(transaction, fields);
                auditService.logVnPayCallback(txnRef, fields, clientIP, true);
                logger.info("Payment successful for transaction: {}", transactionId);
            } else {
                // Failed case
                processFailedPayment(transaction, responseCode);
                auditService.logVnPayCallback(txnRef, fields, clientIP, false);
                logger.warn("Payment failed for transaction: {}, response code: {}", transactionId, responseCode);
            }

            // Mark as processed to prevent replay
            securityService.markTransactionAsProcessed(txnRef);

        } catch (PaymentProcessingException e) {
            logger.error("Payment processing error in callback", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing VNPay callback", e);
            throw new PaymentProcessingException("Failed to confirm VNPay payment: " + e.getMessage(), "CALLBACK_ERROR", e);
        }
    }

    private void processSuccessfulPayment(Transaction transaction, Map<String, String> fields) {
        try {
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            // Update member subscription
            Member member = transaction.getMember();
            if (member == null) {
                logger.error("Transaction has no member linked: {}", transaction.getTransactionId());
                throw new PaymentProcessingException("Transaction has no member linked", "NO_MEMBER_LINKED");
            }

            // Get subscription from transaction amount (find matching subscription)
            Subscription subscription = subscriptionRepository.findAll().stream()
                    .filter(s -> s.getPrice().compareTo(transaction.getAmount()) == 0)
                    .findFirst()
                    .orElseThrow(() -> new PaymentProcessingException("No matching subscription found", "NO_MATCHING_SUBSCRIPTION"));

            member.setSubscription(subscription);
            member.setStartDate(LocalDateTime.now());
            member.setEndDate(LocalDateTime.now().plusDays(subscription.getDuration()));
            member.setSubscriptionStatus(true); // Dòng này là đúng vì Member.subscriptionStatus là boolean
            memberRepository.save(member);
            logger.info("Updated member subscription status for member: {}", member.getMemberId());

            // Update user role
            User user = member.getUser();
            if (user != null) {
                // Lấy entity.Role từ database
                com.swp391project.SWP391_QuitSmoking_BE.entity.Role entityPremiumRole = roleRepository.findByRoleName("ROLE_PREMIUM")
                        .orElseThrow(() -> {
                            logger.error("Premium role not found in database (entity.Role)");
                            return new PaymentProcessingException("Premium role not found", "ROLE_NOT_FOUND");
                        });

                // Chuyển đổi từ entity.Role sang enums.Role để gán vào User entity
                Role enumPremiumRole = Role.valueOf(entityPremiumRole.getRoleName());

                user.setRole(enumPremiumRole);
                userRepository.save(user);
                logger.info("Updated user role to PREMIUM for user: {}", user.getUserId());
            }
        } catch (Exception e) {
            logger.error("Error processing successful payment", e);
            throw new PaymentProcessingException("Failed to process successful payment", "SUCCESS_PROCESSING_ERROR", e);
        }
    }

    private void processFailedPayment(Transaction transaction, String responseCode) {
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        logger.info("Marked transaction as failed: {}, response code: {}",
                transaction.getTransactionId(), responseCode);
    }

    private void validatePaymentOrderRequest(PaymentOrderRequest request) {
        if (request == null) {
            throw new PaymentProcessingException("Payment request cannot be null", "NULL_REQUEST");
        }
        if (request.getMemberId() == null) {
            throw new PaymentProcessingException("Member ID cannot be null", "NULL_MEMBER_ID");
        }
        if (request.getSubscriptionId() == null) {
            throw new PaymentProcessingException("Subscription ID cannot be null", "NULL_SUBSCRIPTION_ID");
        }
        if (request.getTransactionMethodId() == null) {
            throw new PaymentProcessingException("Transaction method ID cannot be null", "NULL_METHOD_ID");
        }
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        logger.info("Manual payment confirmation for transaction: {}", request.getTransactionId());

        PaymentConfirmResponse response = new PaymentConfirmResponse();
        response.setTransactionId(request.getTransactionId());
        response.setStatus("CONFIRMED");
        response.setMessage("Thanh toán đã được xác nhận thành công.");
        response.setSuccess(true);
        return response;
    }
}