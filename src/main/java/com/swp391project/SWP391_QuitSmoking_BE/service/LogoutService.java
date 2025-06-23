package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.TokenBlackList;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TokenBlacklistRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private static final Logger log = LoggerFactory.getLogger(LogoutService.class);
    private final JwtUtil jwtUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public void blacklistToken(String authHeader) {

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("🔑 Extracted token: {}...", token.substring(0, Math.min(20, token.length())));
            try {
                String jti = jwtUtil.extractJti(token);
                Date expiration = jwtUtil.extractExpiration(token);

                log.debug("JTI extracted: {}", jti);
                log.debug("Expiration: {}", expiration);

                if (jti == null || expiration == null) {
                    log.error("JTI or Expiration Date could not be extracted from token.");
                    return;
                }

                if (tokenBlacklistRepository.findByJti(jti).isPresent()) {
                    log.info("Token already blacklisted");
                    return;
                }

                TokenBlackList tokenBlackList = new TokenBlackList(jti, expiration.toInstant());
                tokenBlacklistRepository.save(tokenBlackList);

                boolean exists = tokenBlacklistRepository.findByJti(jti).isPresent();
                log.debug("🔍 Verification - Token exists in DB: {}", exists);
            } catch (JwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
            } catch (Exception e) {
                log.error("An error occurred while blacklisting the token: {}", e.getMessage(), e);
            }

        } else {
            log.error("Authorization header is missing or does not start with 'Bearer ' type.");
        }

    }
}
