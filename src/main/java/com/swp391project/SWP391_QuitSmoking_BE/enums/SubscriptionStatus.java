package com.swp391project.SWP391_QuitSmoking_BE.enums;

/**
 * Enum đại diện cho trạng thái của một gói đăng ký thành viên.
 */
public enum SubscriptionStatus {
    /**
     * Gói đăng ký đang hoạt động.
     */
    ACTIVE,
    /**
     * Gói đăng ký đã hết hạn.
     */
    EXPIRED,
    /**
     * Gói đăng ký đã bị hủy.
     */
    CANCELLED,
    /**
     * Gói đăng ký đang chờ xử lý (ví dụ: chờ thanh toán).
     */
    PENDING,
    /**
     * Gói đăng ký tạm ngưng.
     */
    PAUSED,
    /**
     * Trạng thái không hoạt động (được thêm để phù hợp với boolean false)
     */
    INACTIVE
}