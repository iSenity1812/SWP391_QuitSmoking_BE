package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.FcmToken;
import com.swp391project.SWP391_QuitSmoking_BE.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;

    public FcmToken saveToken(UUID userId, String token) {
        // Nếu token đã tồn tại, cập nhật userId và createdAt
        Optional<FcmToken> existing = fcmTokenRepository.findByToken(token);
        FcmToken fcmToken = existing.orElseGet(FcmToken::new);
        fcmToken.setUserId(userId);
        fcmToken.setToken(token);
        fcmToken.setCreatedAt(LocalDateTime.now());
        return fcmTokenRepository.save(fcmToken);
    }

    public List<FcmToken> getTokensByUser(UUID userId) {
        return fcmTokenRepository.findByUserId(userId);
    }

    public void deleteToken(String token) {
        fcmTokenRepository.deleteByToken(token);
    }

    public void deleteTokensByUser(UUID userId) {
        fcmTokenRepository.deleteByUserId(userId);
    }
} 