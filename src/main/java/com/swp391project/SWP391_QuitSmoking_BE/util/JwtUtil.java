package com.swp391project.SWP391_QuitSmoking_BE.util;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AuthenticationRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm; // Đảm bảo import này tồn tại
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final AuthenticationRepository authenticationRepository;
    // Thuộc tính này sẽ được inject từ file cấu hình application.properties
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private Long expirationTime;

    public JwtUtil(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public User extractUser(String token) {
        String email = extractClaim(token, Claims::getSubject);
        return authenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractClaim(token, Claims::getSubject);
        String userEmail = null;
        if (userDetails instanceof User) {
            userEmail = ((User) userDetails).getEmail();
        } else {
            userEmail = userDetails.getUsername();
        }
        return (email.equals(userEmail) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        if (userDetails instanceof  User) {
            User user = (User) userDetails;
            extraClaims.put("role", user.getRole());
            extraClaims.put("userId", user.getUserId());
        }
        extraClaims.put("jti", UUID.randomUUID().toString());

        String subject = null;
        if (userDetails instanceof User) {
            subject = ((User) userDetails).getEmail();
        } else {
            subject = userDetails.getUsername();
        }

        // ĐÃ SỬA: Sử dụng setClaims() và SignatureAlgorithm.HS256 trực tiếp
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractJti(String token) {
        return extractClaim(token, claims -> claims.get("jti", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // ĐÃ SỬA: Sử dụng parserBuilder() của JJWT 0.11.5
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
