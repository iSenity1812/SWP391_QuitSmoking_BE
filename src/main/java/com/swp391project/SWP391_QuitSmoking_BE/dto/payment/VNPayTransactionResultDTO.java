package com.swp391project.SWP391_QuitSmoking_BE.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VNPayTransactionResultDTO {
    private String vnp_ResponseCode; // Mã phản hồi từ VNPay
    private String Message; // Thông báo từ VNPay
    private String vnp_TxnRef; // Mã giao dịch của VNPay
    private BigDecimal vnp_Amount; // Số tiền giao dịch
    private String vnp_OrderInfo; // Thông tin đơn hàng
    private String vnp_PayDate; // Ngày thanh toán
    private String vnp_TransactionStatus; // Trạng thái giao dịch (00: thành công, 01: đang chờ xử lý, 02: thất bại, ...)
}