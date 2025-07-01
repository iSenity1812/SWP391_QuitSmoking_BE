package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.payment.VNPayPaymentRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.payment.VNPayPaymentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.payment.VNPayTransactionResultDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import com.swp391project.SWP391_QuitSmoking_BE.service.VNPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "VNPay API", description = "API để tích hợp thanh toán VNPay")
@RequiredArgsConstructor
@RequestMapping("/api/vnpay")
@SecurityRequirement(name = "user_api")

public class VNPayController {
    private static final Logger logger = LoggerFactory.getLogger(VNPayController.class);
    private final VNPayService vnpayService;
    private final UserService userService;

    /**
     * Endpoint để tạo URL thanh toán VNPay.
     * Frontend sẽ gọi API này để lấy URL và chuyển hướng người dùng đến đó.
     *
     * @param paymentRequestDTO DTO chứa thông tin yêu cầu thanh toán (số tiền, ID gói, ID người dùng, v.v.).
     * @param request HttpServletRequest để lấy IP của người dùng.
     * @return ResponseEntity chứa VNPayPaymentResponse với paymentUrl.
     */
    @Operation(summary = "Tạo URL thanh toán VNPay",
            description = "Tạo một yêu cầu thanh toán VNPay và trả về URL để client redirect.")
    @PostMapping("/create-payment")
    @PreAuthorize("hasRole('NORMAL_MEMBER')") // normal member
    public ResponseEntity<ApiResponse<VNPayPaymentResponseDTO>> createPayment(
            @Valid @RequestBody VNPayPaymentRequestDTO paymentRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        try {
            UUID userId = userService.getUserIdFromUserDetails(userDetails);

            logger.info("Received create payment request for user: {} and amount: {}", userId, paymentRequestDTO.getAmount());
            VNPayPaymentResponseDTO responseData = vnpayService.createPaymentUrl(paymentRequestDTO, userId, request);
            return ResponseEntity.ok(ApiResponse.success(responseData, "URL thanh toán VNPay đã được tạo thành công."));
        } catch (UnsupportedEncodingException e) {
            logger.error("Error creating VNPAY payment URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi tạo URL thanh toán.", e.getMessage(), "VNPAY_URL_ENCODING_ERROR"));
        } catch (Exception e) {
            logger.error("An unexpected error occurred while creating VNPAY payment URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không mong muốn.", e.getMessage(), "UNEXPECTED_ERROR"));
        }
    }

    @Operation(summary = "Xử lý Return URL từ VNPay",
            description = "Endpoint này được VNPay gọi sau khi khách hàng hoàn tất thanh toán. Đây là callback qua trình duyệt.")
    @GetMapping("/payment-return")
    public ResponseEntity<ApiResponse<VNPayTransactionResultDTO>> paymentReturn(HttpServletRequest request) {
        logger.info("Received VNPAY return callback.");
        try {
            VNPayTransactionResultDTO resultData = vnpayService.processPaymentReturn(request);
            // Dựa vào RspCode để trả về thông báo phù hợp
            if ("00".equals(resultData.getVnp_ResponseCode())) {
                return ResponseEntity.ok(ApiResponse.success(resultData, "Giao dịch VNPay thành công."));
            } else if ("97".equals(resultData.getVnp_ResponseCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Chữ ký VNPay không hợp lệ.", resultData.getMessage(), "VNPAY_INVALID_SIGNATURE"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Giao dịch VNPay thất bại.", resultData.getMessage(), "VNPAY_TRANSACTION_FAILED"));
            }
        } catch (Exception e) {
            logger.error("An unexpected error occurred while processing VNPAY return: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không mong muốn khi xử lý kết quả thanh toán.", e.getMessage(), "UNEXPECTED_ERROR"));
        }
    }

    @Operation(summary = "Xử lý IPN từ VNPay (Instant Payment Notification)",
            description = "Endpoint này được VNPay gọi từ server của họ để xác nhận kết quả giao dịch. Đây là callback chính thức và đáng tin cậy.")
    @GetMapping("/ipn")
    public ResponseEntity<String> receiveIPN(HttpServletRequest request) {
        logger.info("Received VNPAY IPN callback.");
        String responseForVNPay = vnpayService.processIPN(request);
        // VNPay mong đợi một response dạng JSON hoặc text đơn giản "00" để xác nhận đã nhận được IPN.
        // Bất kỳ response nào khác "00" có thể khiến VNPay gửi lại IPN.
        // Do đó, chúng ta trả về String trực tiếp từ service, không bọc trong ApiResponse.
        return ResponseEntity.ok(responseForVNPay);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ApiResponse.validationError(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào không hợp lệ.", errors);
    }
}
