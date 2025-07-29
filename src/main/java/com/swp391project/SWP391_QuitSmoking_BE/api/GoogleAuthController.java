package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.request.GoogleAuthRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.GoogleAuthRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.AuthResponse;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    /**
     * Đăng nhập/đăng ký bằng Google OAuth
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleAuthRequest request) {
        try {
            AuthResponse response = googleAuthService.authenticateWithGoogle(
                    request.getGoogleId(),
                    request.getEmail(),
                    request.getName(),
                    request.getPicture()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Google authentication failed: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Liên kết tài khoản Google với tài khoản hiện tại
     */
    @PostMapping("/link")
    public ResponseEntity<AuthResponse> linkGoogleAccount(@RequestBody GoogleAuthRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            AuthResponse response = googleAuthService.linkGoogleAccount(request.getGoogleId(), user.getUserId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Failed to link Google account: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Hủy liên kết tài khoản Google
     */
    @DeleteMapping("/unlink")
    public ResponseEntity<AuthResponse> unlinkGoogleAccount(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            googleAuthService.unlinkGoogleAccount(user.getUserId());
            return ResponseEntity.ok(AuthResponse.builder().success(true).message("Unlinked Google account successfully").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Failed to unlink Google account: " + e.getMessage())
                            .build());
        }
    }
} 