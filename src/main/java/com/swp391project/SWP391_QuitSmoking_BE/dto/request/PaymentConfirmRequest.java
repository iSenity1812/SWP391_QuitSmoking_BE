package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import lombok.Data; // Import Lombok Data
import jakarta.validation.constraints.NotNull; // Import nếu bạn muốn thêm validation
import java.util.UUID; // Import UUID

// Sử dụng @Data của Lombok để tự động tạo getter, setter, toString, equals, hashCode
@Data
public class PaymentConfirmRequest {
    @NotNull // Đảm bảo transactionId không null khi nhận request
    private UUID transactionId; // ID giao dịch
    private String responseCode; // Mã phản hồi từ VNPay (ví dụ: "00" cho thành công)
    private String transactionStatus; // Trạng thái giao dịch (ví dụ: "00", "SUCCESS", "FAILED")
    private Integer amount; // Số tiền giao dịch (Integer phù hợp với kiểu dữ liệu số nguyên)

    // Các trường khác có thể thêm vào nếu cần nhận thêm thông tin từ callback VNPay
    // Ví dụ: private String vnp_PayDate;
    // private String vnp_TransactionNo;
    // private String vnp_OrderInfo;
}
