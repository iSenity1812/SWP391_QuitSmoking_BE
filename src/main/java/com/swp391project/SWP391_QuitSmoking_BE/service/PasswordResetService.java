package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
import com.swp391project.SWP391_QuitSmoking_BE.entity.PasswordResetToken;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AuthenticationRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.PasswordResetTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    
    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private AuthenticationRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${application.frontend.url:http://localhost:5173}")
    private String frontendUrl;
    
    @Value("${application.password-reset.expiry-hours:24}")
    private int expiryHours;
    
    @Transactional
    public void sendForgotPasswordEmail(String email) {
        log.info("Processing forgot password request for email: {}", email);
        
        // Kiểm tra user có tồn tại không
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Forgot password requested for non-existent email: {}", email);
            // Không throw exception để tránh email enumeration attack
            return;
        }
        
        User user = userOpt.get();
        
        // Xóa các token cũ của email này
        tokenRepository.markAllTokensAsUsedForEmail(email);
        
        // Tạo token mới
        String token = generateToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(expiryHours);
        
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(expiryDate);
        resetToken.setUsed(false);
        
        tokenRepository.save(resetToken);
        log.info("Created password reset token for email: {}", email);
        
        // Gửi email
        sendResetPasswordEmail(user, token);
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.info("Processing password reset for token: {}", token);
        
        // Tìm token
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn");
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        // Kiểm tra token đã được sử dụng chưa
        if (resetToken.isUsed()) {
            throw new RuntimeException("Token đã được sử dụng");
        }
        
        // Kiểm tra token có hết hạn không
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn");
        }
        
        // Tìm user
        Optional<User> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        
        User user = userOpt.get();
        
        // Cập nhật mật khẩu
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Đánh dấu token đã sử dụng
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        // Xóa tất cả token cũ của email này
        tokenRepository.markAllTokensAsUsedForEmail(resetToken.getEmail());
        
        log.info("Password reset successful for user: {}", user.getEmail());
    }
    
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        return !resetToken.isUsed() && resetToken.getExpiryDate().isAfter(LocalDateTime.now());
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    private void sendResetPasswordEmail(User user, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("username", user.getUsername());
            templateVariables.put("resetLink", resetLink);
            templateVariables.put("expiryHours", expiryHours);
            templateVariables.put("websiteUrl", frontendUrl);
            
            EmailDetail emailDetail = new EmailDetail(
                user.getEmail(),
                "Đặt lại mật khẩu - QuitTogether",
                null,
                "passwordResetTemplate",
                templateVariables
            );
            
            emailService.sendEmail(emailDetail);
            log.info("Password reset email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email đặt lại mật khẩu: " + e.getMessage());
        }
    }
    
    // Cleanup expired tokens (có thể gọi định kỳ)
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Cleaned up expired password reset tokens");
    }
} 