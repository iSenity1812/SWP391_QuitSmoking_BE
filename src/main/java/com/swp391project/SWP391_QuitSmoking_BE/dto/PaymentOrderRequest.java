package com.swp391project.SWP391_QuitSmoking_BE.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class PaymentOrderRequest {
    @NotNull
    private UUID memberId;
    @NotNull
    private Integer subscriptionId;
    @NotNull
    private Integer transactionMethodId;

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Integer getTransactionMethodId() {
        return transactionMethodId;
    }

    public void setTransactionMethodId(Integer transactionMethodId) {
        this.transactionMethodId = transactionMethodId;
    }
}