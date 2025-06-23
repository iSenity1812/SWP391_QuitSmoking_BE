package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data // Tự động tạo getters, setters, toString, equals và hashCode
@NoArgsConstructor // Quan trọng: Tự động tạo constructor không tham số
@AllArgsConstructor // Quan trọng: Tự động tạo constructor với tất cả các trường
@Builder // Nếu bạn có ý định dùng Builder pattern
public class PaymentOrderResponse {
    private UUID transactionId;
    private BigDecimal amount;
    private String status;
    private String subscriptionName;
    private String transactionMethod;
    private String paymentUrl;

    // Đảm bảo KHÔNG CÓ BẤT KỲ CONSTRUCTOR NÀO BẠN TỰ ĐỊNH NGHĨA Ở ĐÂY.
    // Nếu có, hãy xóa nó hoàn toàn để Lombok có thể hoạt động đúng.
}