package com.swp391project.SWP391_QuitSmoking_BE.enums;

public enum TransactionStatus {
    PENDING("PENDING", "Giao dịch đang chờ xử lý"),
    PROCESSING("PROCESSING", "Giao dịch đang được xử lý"),
    SUCCESS("SUCCESS", "Giao dịch thành công"),
    FAILED("FAILED", "Giao dịch thất bại"),
    CANCELLED("CANCELLED", "Giao dịch đã hủy"),
    EXPIRED("EXPIRED", "Giao dịch đã hết hạn"),
    REFUNDED("REFUNDED", "Giao dịch đã hoàn tiền"),
    PARTIAL_REFUND("PARTIAL_REFUND", "Hoàn tiền một phần");

    private final String code;
    private final String description;

    TransactionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TransactionStatus fromCode(String code) {
        for (TransactionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown transaction status code: " + code);
    }

    public boolean isCompleted() {
        return this == SUCCESS || this == FAILED || this == CANCELLED || this == EXPIRED;
    }

    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    public boolean canBeRefunded() {
        return this == SUCCESS;
    }
}
