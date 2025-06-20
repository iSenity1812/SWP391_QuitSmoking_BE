package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data // Sử dụng Lombok để tự động tạo getter/setter/toString/equals/hashCode
public class PaymentOrderRequest {
    @NotNull
    private UUID memberId;
    @NotNull
    private Integer subscriptionId;
    @NotNull
    private Integer transactionMethodId; // Phương thức giao dịch (ví dụ: VNPay, PayPal)

    @NotNull
    private Double amount; // Số tiền cần thanh toán
    private String orderInfo; // Thông tin mô tả đơn hàng
    private String bankCode; // Mã ngân hàng (nếu có, ví dụ: VNPAYQR, NCB)
    private String language; // Ngôn ngữ hiển thị trên cổng VNPay (vn/en)

    // Các trường liên quan đến thông tin khách hàng cho VNPay (tùy chọn)
    private String billingFullname;
    private String billingEmail;
    private String billingMobile;

    // Getters và setters được tự động tạo bởi Lombok @Data.
    // Nếu không dùng Lombok, bạn cần viết thủ công như code cũ của bạn.
}