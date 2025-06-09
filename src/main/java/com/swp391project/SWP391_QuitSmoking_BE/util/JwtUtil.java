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
        // Logic để trích xuất username từ token
        // Ví dụ: sử dụng JWT parser để lấy thông tin từ token
        return extractClaim(token, Claims::getSubject); // Trả về username đã trích xuất
    }

//    public String extractEmail(String token) {
//        // Logic để trích xuất email từ token
//        // Ví dụ: sử dụng JWT parser để lấy thông tin từ token
//        return extractClaim(token, Claims::getSubject); // Trả về email đã trích xuất
//    }

    public User extractUser(String token) {
        String email = extractClaim(token, Claims::getSubject);
        return authenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Kiểm tra token có hợp lệ hay không -
    public Boolean validateToken(String token, UserDetails userDetails) {
        // Lấy username từ token
//        final String username = extractUsername(token);
        final String email = extractClaim(token, Claims::getSubject);
        // Lấy email từ userDetails
        String userEmail = null;
        if (userDetails instanceof User) {
            // Nếu userDetails là một đối tượng User, lấy email từ đó
            userEmail = ((User) userDetails).getEmail();
        } else {
            // Nếu không, lấy username từ userDetails
            userEmail = userDetails.getUsername();
        }
        // Kiểm tra xem username trong token có khớp với userDetails không
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        return (email.equals(userEmail) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    //
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        if (userDetails instanceof  User) {
            User user = (User) userDetails;
            extraClaims.put("role", user.getRole()); // Thêm role vào claims nếu userDetails là User
            extraClaims.put("userId", user.getUserId()); // Thêm userId vào claims
        }
        // Thêm JTI
        extraClaims.put("jti", UUID.randomUUID().toString());

        String subject = null;
        if (userDetails instanceof User) {
            // Nếu userDetails là một đối tượng User, lấy email từ đó
            subject = ((User) userDetails).getEmail();
        } else {
            // Nếu không, lấy username từ userDetails
            subject = userDetails.getUsername();
        }

        return Jwts
                .builder()
                .claims(extraClaims) // Thêm các claims bổ sung vào token
                .subject(subject) // Thiết lập subject (email)
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

    public String extractJti(String token) {
        // Trích xuất JTI từ token
        return extractClaim(token, claims -> claims.get("jti", String.class));
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
