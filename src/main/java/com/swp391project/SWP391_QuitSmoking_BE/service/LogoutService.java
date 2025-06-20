package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.TokenBlackList;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TokenBlacklistRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final JwtUtil jwtUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public void blacklistToken(String authHeader) {

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("🔑 Extracted token: " + token.substring(0, 20) + "...");
            try {
                String jti = jwtUtil.extractJti(token);
                Date expiration = jwtUtil.extractExpiration(token);

                System.out.println("JTI extracted: " + jti);
                System.out.println("Expiration: " + expiration);

                if (jti == null || expiration == null) {
                    System.err.println("JTI or Expiration Date could not be extracted from token.");
                    return;
                }

                if (tokenBlacklistRepository.findByJti(jti).isPresent()) {
                    System.out.println("Token already blacklisted");
                    return;
                }

                TokenBlackList tokenBlackList = new TokenBlackList(jti, expiration.toInstant());
                tokenBlacklistRepository.save(tokenBlackList);

                boolean exists = tokenBlacklistRepository.findByJti(jti).isPresent();
                System.out.println("🔍 Verification - Token exists in DB: " + exists);
            } catch (JwtException e) {
                System.err.println("Invalid JWT token: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An error occurred while blacklisting the token: " + e.getMessage());
            }

        } else {
            System.err.println("Authorization header is missing or does not start with 'Bearer ' type.");
        }

    }
}
