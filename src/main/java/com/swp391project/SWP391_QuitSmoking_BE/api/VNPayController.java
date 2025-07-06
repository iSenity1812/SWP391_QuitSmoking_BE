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
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public RedirectView paymentReturn(HttpServletRequest request) {
        logger.info("Received VNPAY return callback for browser redirect.");
        // Đảm bảo URL này là URL của trang kết quả trên frontend của bạn
        String frontendRedirectBaseUrl = "http://localhost:5173/payment-return";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(frontendRedirectBaseUrl);

        try {
            VNPayTransactionResultDTO resultData = vnpayService.processPaymentReturn(request);

            // Mã hóa từng tham số trước khi thêm vào URL frontend
            uriBuilder.queryParam("vnp_ResponseCode", URLEncoder.encode(resultData.getVnp_ResponseCode() != null ? resultData.getVnp_ResponseCode() : "", StandardCharsets.UTF_8.toString()));
            uriBuilder.queryParam("vnp_TxnRef", URLEncoder.encode(resultData.getVnp_TxnRef() != null ? resultData.getVnp_TxnRef() : "", StandardCharsets.UTF_8.toString()));
            uriBuilder.queryParam("vnp_Amount", URLEncoder.encode(resultData.getVnp_Amount() != null ? resultData.getVnp_Amount().toPlainString() : "", StandardCharsets.UTF_8.toString()));
            uriBuilder.queryParam("vnp_OrderInfo", URLEncoder.encode(resultData.getVnp_OrderInfo() != null ? resultData.getVnp_OrderInfo() : "", StandardCharsets.UTF_8.toString()));
            uriBuilder.queryParam("vnp_PayDate", URLEncoder.encode(resultData.getVnp_PayDate() != null ? resultData.getVnp_PayDate() : "", StandardCharsets.UTF_8.toString()));
            uriBuilder.queryParam("vnp_TransactionStatus", URLEncoder.encode(resultData.getVnp_TransactionStatus() != null ? resultData.getVnp_TransactionStatus() : "", StandardCharsets.UTF_8.toString()));
            uriBuilder.queryParam("message", URLEncoder.encode(resultData.getMessage() != null ? resultData.getMessage() : "", StandardCharsets.UTF_8.toString()));

            // Thêm một tham số tổng quan về trạng thái để frontend dễ xử lý
            if ("00".equals(resultData.getVnp_ResponseCode()) && "SUCCESS".equals(resultData.getVnp_TransactionStatus())) {
                uriBuilder.queryParam("status", "success");
            } else {
                uriBuilder.queryParam("status", "failed");
            }

        } catch (Exception e) {
            logger.error("An unexpected error occurred while redirecting VNPAY return: {}", e.getMessage());
            uriBuilder = UriComponentsBuilder.fromUriString(frontendRedirectBaseUrl);
            uriBuilder.queryParam("status", "error");
            try {
                uriBuilder.queryParam("message", URLEncoder.encode("Đã xảy ra lỗi không mong muốn từ hệ thống: " + e.getMessage(), StandardCharsets.UTF_8.toString()));
            } catch (Exception encodeError) {
                logger.error("Error encoding error message for redirect: {}", encodeError.getMessage());
                uriBuilder.queryParam("message", "Đã xảy ra lỗi không mong muốn.");
            }
        }

        String redirectUrl = uriBuilder.toUriString();
        logger.info("Redirecting browser to frontend URL: {}", redirectUrl);
        return new RedirectView(redirectUrl);
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
