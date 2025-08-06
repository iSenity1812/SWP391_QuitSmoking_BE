package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "Test API", description = "API để test các chức năng")
@Slf4j
public class TestController {

    private final EmailService emailService;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @GetMapping("/email-config")
    @Operation(summary = "Test email configuration", description = "Kiểm tra cấu hình email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testEmailConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("host", mailHost);
            config.put("port", mailPort);
            config.put("username", mailUsername);
            config.put("passwordConfigured", mailUsername != null && !mailUsername.isEmpty());

            if (javaMailSender instanceof JavaMailSenderImpl) {
                JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) javaMailSender;
                config.put("javaMailSenderType", "JavaMailSenderImpl");
                config.put("senderUsername", mailSenderImpl.getUsername());
                config.put("senderHost", mailSenderImpl.getHost());
                config.put("senderPort", mailSenderImpl.getPort());
            } else {
                config.put("javaMailSenderType", "Unknown");
            }

            log.info("Email configuration test completed successfully");
            return ResponseEntity.ok(ApiResponse.success(config, "Email configuration test thành công"));
        } catch (Exception e) {
            log.error("Error testing email configuration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi test email configuration: " + e.getMessage()));
        }
    }

    @PostMapping("/send-test-email")
    @Operation(summary = "Send test email", description = "Gửi email test")
    public ResponseEntity<ApiResponse<Void>> sendTestEmail(@RequestParam String recipientEmail) {
        try {
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("username", "Test User");
            templateVariables.put("testMessage", "Đây là email test từ QuitTogether");
            templateVariables.put("currentDate", java.time.LocalDateTime.now().toString());

            EmailDetail emailDetail = new EmailDetail(
                recipientEmail,
                "🧪 Test Email - QuitTogether",
                null,
                "testEmailTemplate",
                templateVariables
            );

            emailService.sendEmail(emailDetail);
            log.info("Test email sent successfully to: {}", recipientEmail);
            return ResponseEntity.ok(ApiResponse.success("Email test đã được gửi thành công đến: " + recipientEmail));
        } catch (Exception e) {
            log.error("Error sending test email: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi gửi email test: " + e.getMessage()));
        }
    }

    @GetMapping("/health-status")
    @Operation(summary = "Test health status", description = "Kiểm tra trạng thái hệ thống")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testHealthStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("status", "UP");
            status.put("timestamp", java.time.LocalDateTime.now().toString());
            status.put("emailService", "Configured");
            status.put("smtpHost", mailHost);
            status.put("smtpPort", mailPort);

            log.info("Health status test completed successfully");
            return ResponseEntity.ok(ApiResponse.success(status, "Health status test thành công"));
        } catch (Exception e) {
            log.error("Error testing health status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi test health status: " + e.getMessage()));
        }
    }
}
