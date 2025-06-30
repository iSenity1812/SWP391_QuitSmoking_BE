package com.swp391project.SWP391_QuitSmoking_BE.exception;

import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý ngoại lệ validation (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // Sử dụng phương thức validationError mới
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(HttpStatus.BAD_REQUEST, "Dữ liệu gửi lên không hợp lệ", errors));
    }

    // Xử lý ngoại lệ email đã tồn tại
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmailException(DuplicateEmailException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT, "Đăng ký thất bại", ex.getMessage(), "DUPLICATE_EMAIL")); // Thêm errorCode
    }

    // Xử lý ngoại lệ username đã tồn tại (ví dụ: DuplicateUsernameException)
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT, "Đăng ký thất bại", ex.getMessage(), "DUPLICATE_USERNAME")); // Thêm errorCode
    }

    // Xử lý ngoại lệ thông tin đăng nhập không hợp lệ
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Xác thực thất bại", ex.getMessage(), "INVALID_CREDENTIALS"));
    }

    // Xử lý ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy tài nguyên", ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    // Xử lý PasswordMismatchException
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handlePasswordMismatchException(PasswordMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Lỗi yêu cầu", ex.getMessage(), "PASSWORD_MISMATCH"));
    }

    // Xử lý CravingTrackingEditForbiddenException
    @ExceptionHandler(CravingTrackingEditForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleTrackingEditForbiddenException(CravingTrackingEditForbiddenException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Không thể chỉnh sửa bảng theo dõi", ex.getMessage(), "TRACKING_EDIT_FORBIDDEN"));
    }

    // Xử lý DailySummaryEditForbiddenException
    @ExceptionHandler(DailySummaryEditForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleDailySummaryEditForbiddenException(DailySummaryEditForbiddenException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Không thể chỉnh sửa bảng hằng ngày", ex.getMessage(), "DAILYSUMMARY_EDIT_FORBIDDEN"));
    }

    // Xử lý việc hệ thống xóa bảng ghi khi người dùng chỉnh sửa giá trị về 0
    @ExceptionHandler(CravingTrackingDeletedException.class)
    public ResponseEntity<ApiResponse<Void>> handleTrackingDeletedException(CravingTrackingDeletedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Bảng ghi đã bị xóa", ex.getMessage(), "TRACKING_DELETED"));
    }

    // Xử lý việc hệ thống xóa bảng ghi khi người dùng chỉnh sửa giá trị về 0
    @ExceptionHandler(DailySummaryDeletedException.class)
    public ResponseEntity<ApiResponse<Void>> handleSummaryDeletedException(DailySummaryDeletedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Nhật ký đã bị xóa", ex.getMessage(), "SUMMARY_DELETED"));
    }

    @ExceptionHandler(QuitPlanChangedTypeException.class)
    public ResponseEntity<ApiResponse<Void>> handlePlanTypeChangedException(QuitPlanChangedTypeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "PlanType đã bị thay đổi", ex.getMessage(), "PlANTYPE_CHANGED"));
    }

    // Xử lý DataIntegrityViolationException (ví dụ từ lỗi khóa ngoại, unique constraint)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = "Thao tác không thể hoàn thành do vi phạm ràng buộc dữ liệu.";
        String errorCode = "DATA_INTEGRITY_VIOLATION";

        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String causeMessage = ex.getCause().getMessage().toLowerCase();
            if (causeMessage.contains("foreign key constraint")) {
                errorMessage = "Không thể thực hiện thao tác do có dữ liệu liên quan. Vui lòng kiểm tra các mục phụ thuộc.";
                errorCode = "FOREIGN_KEY_CONSTRAINT";
            } else if (causeMessage.contains("unique constraint")) {
                errorMessage = "Dữ liệu bạn nhập đã tồn tại và yêu cầu phải là duy nhất.";
                errorCode = "UNIQUE_CONSTRAINT_VIOLATION";
            } else if (causeMessage.contains("not null constraint")) {
                errorMessage = "Một trường bắt buộc không được cung cấp.";
                errorCode = "NOT_NULL_CONSTRAINT";
            }
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT, "Lỗi ràng buộc dữ liệu", errorMessage, errorCode));
    }

    // Xử lý các ngoại lệ RuntimeException chung
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        // Log lỗi chi tiết ở đây cho mục đích debug (ví dụ: logger.error("Runtime error: {}", ex.getMessage(), ex);)
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi hệ thống không mong muốn", ex.getMessage(), "UNEXPECTED_ERROR"));
    }

    // Xử lý các ngoại lệ chung khác (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        // Log lỗi chi tiết ở đây cho mục đích debug (ví dụ: logger.error("Generic error: {}", ex.getMessage(), ex);)
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không xác định", ex.getMessage(), "UNKNOWN_ERROR"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // HTTP 403
                .body(ApiResponse.error(
                        HttpStatus.FORBIDDEN,
                        "Truy cập bị từ chối",
                        "Bạn không có quyền truy cập vào tài nguyên này.",
                        "ACCESS_DENIED" // Mã lỗi tùy chỉnh
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        String errorMessage = "Xác thực thất bại. Vui lòng cung cấp thông tin đăng nhập hợp lệ.";
        String errorCode = "AUTHENTICATION_REQUIRED"; // Hoặc UNAUTHENTICATED

        if (ex instanceof BadCredentialsException) {
            errorMessage = ex.getMessage(); // Giữ lại thông báo chi tiết từ BadCredentialsException (ví dụ: "Tài khoản vô hiệu hóa")
            errorCode = "INVALID_CREDENTIALS";
            if (ex.getMessage() != null && ex.getMessage().contains("vô hiệu hóa")) {
                errorCode = "ACCOUNT_DEACTIVATED";
            }
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // HTTP 401
                .body(ApiResponse.error(
                        HttpStatus.UNAUTHORIZED,
                        "Xác thực thất bại",
                        errorMessage,
                        errorCode
                ));
    }
}