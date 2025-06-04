package com.swp391project.SWP391_QuitSmoking_BE.util;


import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AuthenticationRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
        // Logic để trích xuất username từ token
        // Ví dụ: sử dụng JWT parser để lấy thông tin từ token
        return extractClaim(token, Claims::getSubject); // Trả về username đã trích xuất
    }

    public User extractUser(String token) {
        String email = extractClaim(token, Claims::getSubject);
        return authenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Kiểm tra token có hợp lệ hay không -
    public Boolean validateToken(String token, UserDetails userDetails) {
        // Lấy username từ token
        final String username = extractUsername(token);
        // Kiểm tra xem username trong token có khớp với userDetails không
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    //
    public String generateToken(Map<String, Object> extraClaims, UserDetails subject) {
        return Jwts
                .builder()
                .claims(extraClaims) // Thêm các claims bổ sung vào token
                .subject(subject.getUsername()) // Thiết lập subject (thường là username)
                .issuedAt(new Date(System.currentTimeMillis())) // Thời điểm token được phát hành
                .expiration(new Date(System.currentTimeMillis() + expirationTime)) // Thời điểm token hết hạn
                .signWith(getSignInKey()) //Ký token bằng khóa bí mật và thuật toán HMAC SHA-256
                .compact(); // Xây dựng token thành chuỗi
    }

    private boolean isTokenExpired(String token) {
        // Kiểm tra xem token đã hết hạn hay chưa
        return extractExpiration(token).before(new Date());
    }

    // Kiểm tra token có hợp lệ hay không - Kiểm tra thời gian hết hạn
    public Date extractExpiration(String token) {
        // Sử dụng hàm extractClaim để lấy thời gian hết hạn từ token
        return extractClaim(token, Claims::getExpiration);
    }

    // Lấy tất cả các claims từ token -> Áp dụng hàm claimsResolver để lấy claim cụ thể
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser() // Bắt đầu xây dựng một JWT parser
                .verifyWith(getSignInKey()) // Xác thực token với khóa bí mật
                .build() // Xây dựng parser
                .parseSignedClaims(token) // Phân tích token đã ký và trả về các claims
                .getPayload(); // Lấy phần body của token, chứa các claims
    }



    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Tạo một khóa HMAC SHA từ mảng byte đã giải mã
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
