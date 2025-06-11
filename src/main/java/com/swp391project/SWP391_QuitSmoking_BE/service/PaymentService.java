package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentOrderRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentOrderResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentConfirmRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.PaymentConfirmResponse;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private TransactionMethodRepository transactionMethodRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public PaymentOrderResponse createOrder(PaymentOrderRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        TransactionMethod method = transactionMethodRepository.findById(request.getTransactionMethodId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction method not found"));

        Transaction transaction = new Transaction();
        transaction.setMember(member);
        transaction.setTransactionMethod(method);
        transaction.setAmount(subscription.getPrice());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction = transactionRepository.save(transaction);

        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setAmount(transaction.getAmount());
        response.setStatus(transaction.getStatus().name());
        response.setSubscriptionName(subscription.getName());
        response.setTransactionMethod(method.getMethodName());
        return response;
    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction is not in PENDING state");
        }
        if (request.isSuccess()) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            // Cập nhật subscription cho member
            Member member = transaction.getMember();
            Subscription subscription = member.getSubscription();
            if (subscription == null || !subscription.getSubscriptionId()
                    .equals(transaction.getMember().getSubscription().getSubscriptionId())) {
                // Gán subscription mới nếu member chưa có hoặc khác
                Subscription paidSub = subscriptionRepository
                        .findById(transaction.getMember().getSubscription().getSubscriptionId())
                        .orElse(null);
                if (paidSub != null) {
                    member.setSubscription(paidSub);
                    member.setStartDate(LocalDateTime.now());
                    // Tính endDate dựa trên duration
                    member.setEndDate(LocalDateTime.now().plusDays(paidSub.getDuration()));
                    member.setSubscriptionStatus(true);
                    memberRepository.save(member);
                }
            }
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
        }
        transactionRepository.save(transaction);
        PaymentConfirmResponse response = new PaymentConfirmResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setStatus(transaction.getStatus().name());
        response.setMessage(request.getMessage() != null ? request.getMessage()
                : (request.isSuccess() ? "Payment successful" : "Payment failed"));
        return response;
    }
}