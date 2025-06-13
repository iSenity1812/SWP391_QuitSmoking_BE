package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final VnPayService vnPayService;
    private final TransactionMethodRepository transactionMethodRepository;

    public PaymentService(
            TransactionRepository transactionRepository,
            SubscriptionRepository subscriptionRepository,
            MemberRepository memberRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            VnPayService vnPayService,
            TransactionMethodRepository transactionMethodRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.vnPayService = vnPayService;
        this.transactionMethodRepository = transactionMethodRepository;
    }

    @Transactional
    public PaymentOrderResponse createOrder(PaymentOrderRequest request) {
        logger.info("Creating payment order for subscription ID: {}, member ID: {}",
                request.getSubscriptionId(), request.getMemberId());

        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> {
                    logger.error("Subscription not found with ID: {}", request.getSubscriptionId());
                    return new IllegalArgumentException("Subscription not found");
                });

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> {
                    logger.error("Member not found with ID: {}", request.getMemberId());
                    return new IllegalArgumentException("Member not found");
                });

        TransactionMethod vnPayMethod = transactionMethodRepository.findByMethodName("VNPAY")
                .orElseThrow(() -> {
                    logger.error("Transaction method VNPAY not found");
                    return new RuntimeException("Transaction method VNPAY not found");
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

        // Create VNPay request
        VnPayRequest vnPayRequest = new VnPayRequest();
        vnPayRequest.setAmount(subscription.getPrice().intValue());
        vnPayRequest.setOrderInfo("Thanh toán gói: " + subscription.getName());
        vnPayRequest.setTransactionId(transactionId.toString());
        vnPayRequest.setOrderType("billpayment");
        vnPayRequest.setLanguage("vn");

        try {
            // Get client IP (in a real app, get this from HttpServletRequest)
            String clientIp = "127.0.0.1";

            // Generate payment URL
            String paymentUrl = vnPayService.createPaymentUrl(vnPayRequest, clientIp);

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
        } catch (Exception e) {
            logger.error("Failed to create payment URL", e);
            throw new RuntimeException("Failed to create payment URL: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void confirmVnPayCallback(Map<String, String> fields) {
        logger.info("Processing VNPay callback with fields: {}", fields);

        try {
            // Extract transaction ID from VNPay response
            String txnRef = fields.get("vnp_TxnRef");
            if (txnRef == null || txnRef.isEmpty()) {
                logger.error("Missing transaction reference in VNPay callback");
                throw new IllegalArgumentException("Missing transaction reference");
            }

            UUID transactionId;
            try {
                transactionId = UUID.fromString(txnRef);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid transaction ID format: {}", txnRef);
                throw new IllegalArgumentException("Invalid transaction ID format");
            }

            // Find transaction
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> {
                        logger.error("Transaction not found with ID: {}", transactionId);
                        return new IllegalArgumentException("Transaction not found");
                    });

            // Check if already processed
            if (transaction.getStatus() == TransactionStatus.SUCCESS) {
                logger.info("Transaction {} already marked as successful, skipping", transactionId);
                return;
            }

            // Process response code
            String responseCode = fields.get("vnp_ResponseCode");
            logger.info("VNPay response code: {}", responseCode);

            if ("00".equals(responseCode)) {
                // Success case
                logger.info("Payment successful for transaction: {}", transactionId);
                transaction.setStatus(TransactionStatus.SUCCESS);
                transaction.setTransactionDate(LocalDateTime.now());
                transactionRepository.save(transaction);

                // Update member subscription
                Member member = transaction.getMember();
                if (member == null) {
                    logger.error("Transaction has no member linked: {}", transactionId);
                    throw new RuntimeException("Transaction has no member linked");
                }

                Subscription subscription = member.getSubscription();
                if (subscription != null) {
                    Subscription paidSub = subscriptionRepository.findById(subscription.getSubscriptionId()).orElse(null);
                    if (paidSub != null) {
                        member.setSubscription(paidSub);
                        member.setStartDate(LocalDateTime.now());
                        member.setEndDate(LocalDateTime.now().plusDays(paidSub.getDuration()));
                        member.setSubscriptionStatus(true);
                        memberRepository.save(member);
                        logger.info("Updated member subscription status for member: {}", member.getMemberId());
                    }
                }

                // Update user role
                User user = member.getUser();
                if (user != null) {
                    Role premiumRole = roleRepository.findByRoleName("ROLE_PREMIUM")
                            .orElseThrow(() -> {
                                logger.error("Premium role not found");
                                return new RuntimeException("Premium role not found");
                            });
                    user.setRole(premiumRole);
                    userRepository.save(user);
                    logger.info("Updated user role to PREMIUM for user: {}", user.getUserId());
                }
            } else {
                // Failed case
                logger.warn("Payment failed for transaction: {}, response code: {}", transactionId, responseCode);
                transaction.setStatus(TransactionStatus.FAILED);
                transaction.setTransactionDate(LocalDateTime.now());
                transactionRepository.save(transaction);
            }

        } catch (Exception e) {
            logger.error("Error processing VNPay callback", e);
            throw new RuntimeException("Failed to confirm VNPay payment: " + e.getMessage(), e);
        }
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        logger.info("Manual payment confirmation for transaction: {}", request.getTransactionId());

        PaymentConfirmResponse response = new PaymentConfirmResponse();
        response.setTransactionId(request.getTransactionId());
        response.setStatus("CONFIRMED");
        response.setMessage("Thanh toán đã được xác nhận thành công.");
        return response;
    }
}