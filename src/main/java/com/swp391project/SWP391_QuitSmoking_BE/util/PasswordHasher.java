package com.swp391project.SWP391_QuitSmoking_BE.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Hash some example passwords
        String password1 = "password123";
        String password2 = "admin123";

        System.out.println("Original: " + password1);
        System.out.println("Hashed: " + encoder.encode(password1));
        System.out.println();
        System.out.println("Original: " + password2);
        System.out.println("Hashed: " + encoder.encode(password2));
    }
}