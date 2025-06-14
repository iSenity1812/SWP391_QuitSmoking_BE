package com.swp391project.SWP391_QuitSmoking_BE.dto;

import lombok.Data;

@Data
public class VnPayRequest {
    private int amount;
    private String orderInfo;
    private String orderType;
    private String bankCode;
    private String language;
    private String billingFullname;
    private String billingEmail;
    private String billingMobile;
    private String transactionId; // Added field for tracking

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBillingFullname() {
        return billingFullname;
    }

    public void setBillingFullname(String billingFullname) {
        this.billingFullname = billingFullname;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    public String getBillingMobile() {
        return billingMobile;
    }

    public void setBillingMobile(String billingMobile) {
        this.billingMobile = billingMobile;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
