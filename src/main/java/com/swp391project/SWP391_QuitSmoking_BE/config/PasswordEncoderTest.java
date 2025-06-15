package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "anhduy@12A1"; // Mật khẩu gốc bạn muốn
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Mật khẩu đã mã hóa: " + encodedPassword);
    }
}