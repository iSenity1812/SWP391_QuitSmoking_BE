package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Scheduled service để tự động cập nhật health metrics
 * 
 * Chức năng:
 * - Cập nhật tiến độ health metrics hàng ngày
 * - Đảm bảo dữ liệu sức khỏe luôn được cập nhật chính xác
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HealthMetricScheduledService {
    
    private final HealthMetricService healthMetricService;
    private final UserRepository userRepository;
    
    /**
     * Cập nhật health metrics cho tất cả users có quit plan
     * Chạy mỗi giờ để đảm bảo tiến độ được cập nhật chính xác
     */
    @Scheduled(cron = "0 0 * * * ?") // Chạy mỗi giờ
    @Transactional
    public void updateAllHealthMetrics() {
        log.info("Bắt đầu cập nhật health metrics cho tất cả users...");
        
        try {
            // Lấy tất cả users có quit plan
            List<User> usersWithQuitPlans = userRepository.findUsersWithActiveQuitPlans();
            
            log.info("Tìm thấy {} users có quit plan active", usersWithQuitPlans.size());
            
            int successCount = 0;
            int errorCount = 0;
            
            for (User user : usersWithQuitPlans) {
                try {
                    healthMetricService.updateHealthMetricsProgress(user);
                    successCount++;
                } catch (Exception e) {
                    log.error("Lỗi khi cập nhật health metrics cho user {}: {}", user.getUserId(), e.getMessage());
                    errorCount++;
                }
            }
            
            log.info("Hoàn thành cập nhật health metrics. Thành công: {}, Lỗi: {}", successCount, errorCount);
            
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật health metrics: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Cập nhật health metrics cho tất cả users có quit plan
     * Chạy mỗi ngày lúc 00:00 để đảm bảo milestone được cập nhật chính xác
     */
    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày lúc 00:00
    @Transactional
    public void dailyHealthMetricsUpdate() {
        log.info("Bắt đầu cập nhật health metrics hàng ngày...");
        
        try {
            // Lấy tất cả users có quit plan
            List<User> usersWithQuitPlans = userRepository.findUsersWithActiveQuitPlans();
            
            log.info("Tìm thấy {} users có quit plan active cho cập nhật hàng ngày", usersWithQuitPlans.size());
            
            int successCount = 0;
            int errorCount = 0;
            
            for (User user : usersWithQuitPlans) {
                try {
                    healthMetricService.updateHealthMetricsProgress(user);
                    successCount++;
                } catch (Exception e) {
                    log.error("Lỗi khi cập nhật health metrics hàng ngày cho user {}: {}", user.getUserId(), e.getMessage());
                    errorCount++;
                }
            }
            
            log.info("Hoàn thành cập nhật health metrics hàng ngày. Thành công: {}, Lỗi: {}", successCount, errorCount);
            
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật health metrics hàng ngày: {}", e.getMessage(), e);
        }
    }
} 