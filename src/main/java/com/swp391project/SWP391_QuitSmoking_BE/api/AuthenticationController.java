package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.request.*;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.AccountResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.AuthResponse;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.AuthenticationService;
import com.swp391project.SWP391_QuitSmoking_BE.service.GoogleAuthService;
import com.swp391project.SWP391_QuitSmoking_BE.service.LogoutService;
import com.swp391project.SWP391_QuitSmoking_BE.service.PasswordResetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private final LogoutService logoutService;
    private final PasswordResetService passwordResetService;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AccountResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AccountResponse newUser = authenticationService.registerUser(registerRequest);
        return ResponseEntity
                .status(201) // HTTP 201 Created is standard for successful resource creation
                .body(ApiResponse.success(newUser, "Registration successful."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AccountResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AccountResponse authentication = authenticationService.authenticateUser(loginRequest);
        log.info("User {} logged in successfully", authentication.getUsername());
        return ResponseEntity
                .status(200) // HTTP 200 OK is standard for successful login
                .body(ApiResponse.success(authentication, "Login successful."));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "user_api")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.info("User with token {} is logging out", authHeader);
        logoutService.blacklistToken(authHeader);
        return ResponseEntity.ok(ApiResponse.success(null, "You have been logged out successfully."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.sendForgotPasswordEmail(request.getEmail());
            log.info("Forgot password email sent for: {}", request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(null, "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn."));
        } catch (Exception e) {
            log.error("Error sending forgot password email: {}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.success(null, "Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            log.info("Password reset successful for token: {}", request.getToken());
            return ResponseEntity.ok(ApiResponse.success(null, "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập với mật khẩu mới."));
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<ApiResponse<Boolean>> validateResetToken(@RequestParam String token) {
        try {
            boolean isValid = passwordResetService.validateToken(token);
            return ResponseEntity.ok(ApiResponse.success(isValid, isValid ? "Token hợp lệ" : "Token không hợp lệ hoặc đã hết hạn"));
        } catch (Exception e) {
            log.error("Error validating reset token: {}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.success(false, "Token không hợp lệ"));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        try {
            AuthResponse authResponse = googleAuthService.authenticateWithGoogle(
                    request.getGoogleId(),
                    request.getEmail(),
                    request.getName(),
                    request.getPicture()
            );
            log.info("Google authentication successful for user: {}", authResponse.getUserInfo().getEmail());
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Google authentication successful."));
        } catch (Exception e) {
            log.error("Google authentication failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }
}
