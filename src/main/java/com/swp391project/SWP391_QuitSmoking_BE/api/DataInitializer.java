package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    public CommandLineRunner initDefaultAdminUser() {
        return args -> {
            // Kiểm tra xem đã có tài khoản SUPER_ADMIN chưa
            if (userRepository.findByUsername("superadmin").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("superadmin");
                adminUser.setEmail("superadmin@example.com");
                    adminUser.setPasswordHash(passwordEncoder.encode("StrongAdmin@123")); // Mật khẩu mạnh cho admin
                adminUser.setCreatedAt(LocalDateTime.now());
                adminUser.setUpdatedAt(LocalDateTime.now());
                adminUser.setActive(true);
                adminUser.setRole(Role.SUPER_ADMIN); // <--- Gán vai trò SUPER_ADMIN trực tiếp
                adminUser.setNotificationSetting(Map.of("emailNotifications", true, "pushNotifications", true)); // Khởi tạo

                userRepository.save(adminUser);
                System.out.println("Default SUPER_ADMIN user created: superadmin");
            }
        };
    }
}
