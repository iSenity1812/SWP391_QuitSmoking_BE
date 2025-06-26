package com.swp391project.SWP391_QuitSmoking_BE.enums;

public enum PaymentMethod {
    VNPAY("VNPAY", "VNPay Gateway", true),
    BANK_TRANSFER("BANK_TRANSFER", "Chuyển khoản ngân hàng", false),
    CREDIT_CARD("CREDIT_CARD", "Thẻ tín dụng", false),
    DEBIT_CARD("DEBIT_CARD", "Thẻ ghi nợ", false),
    E_WALLET("E_WALLET", "Ví điện tử", false),
    CASH("CASH", "Tiền mặt", false);

    private final String code;
    private final String displayName;
    private final boolean isActive;

    PaymentMethod(String code, String displayName, boolean isActive) {
        this.code = code;
        this.displayName = displayName;
        this.isActive = isActive;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return isActive;
    }

    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod method : values()) {
            if (method.code.equals(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method code: " + code);
    }

    public boolean isOnlinePayment() {
        return this == VNPAY || this == CREDIT_CARD || this == DEBIT_CARD || this == E_WALLET;
    }

    public boolean requiresCallback() {
        return this == VNPAY;
    }
}