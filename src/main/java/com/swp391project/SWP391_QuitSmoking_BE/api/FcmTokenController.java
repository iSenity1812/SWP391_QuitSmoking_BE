package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fcm-token")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<?> saveFcmToken(@RequestBody Map<String, String> body, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UUID userId = user.getUserId();
        String token = body.get("fcmToken");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Missing fcmToken");
        }
        fcmTokenService.saveToken(userId, token);
        return ResponseEntity.ok().body("FCM token saved");
    }
} 