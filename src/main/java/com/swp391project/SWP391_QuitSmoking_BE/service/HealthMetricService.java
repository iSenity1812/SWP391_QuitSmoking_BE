package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.health.HealthMetricDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.health.HealthOverviewDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.HealthMetric;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.HealthMetricType;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.HealthMetricRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HealthMetricService {
    
    private final HealthMetricRepository healthMetricRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final DailySummaryRepository dailySummaryRepository;
    
    // Penalty factor configuration - hệ số phạt khi hút thuốc
    private static final double PENALTY_PER_DAY = 0.2; // Giảm 20% mỗi ngày hút thuốc
    
    /**
     * Khởi tạo health metrics cho user mới
     */
    public void initializeHealthMetrics(User user) {
        log.info("Initializing health metrics for user: {}", user.getUserId());
        
        for (HealthMetricType metricType : HealthMetricType.values()) {
            if (!healthMetricRepository.existsByUserAndMetricType(user, metricType)) {
                HealthMetric healthMetric = HealthMetric.builder()
                        .user(user)
                        .metricType(metricType)
                        .currentProgress(0.0)
                        .isCompleted(false)
                        .hasRegressed(false)
                        .description(metricType.getDescription())
                        .build();
                
                healthMetricRepository.save(healthMetric);
            }
        }
        
        log.info("Health metrics initialized for user: {}", user.getUserId());
    }
    
    /**
     * Cập nhật tiến độ health metrics dựa trên việc không hút thuốc
     * Health chỉ tăng khi totalSmokedCount = 0 (không hút thuốc)
     */
    public void updateHealthMetricsProgress(User user) {
        log.info("Updating health metrics progress for user: {}", user.getUserId());
        
        // Tìm member của user
        Member member = user.getMember();
        if (member == null) {
            log.warn("No member found for user: {}", user.getUserId());
            return;
        }
        
        Optional<QuitPlan> currentPlan = quitPlanRepository.findActiveQuitPlanByMember(member);
        if (currentPlan.isEmpty()) {
            log.warn("No current quit plan found for user: {}", user.getUserId());
            return;
        }
        
        QuitPlan quitPlan = currentPlan.get();
        LocalDateTime quitDate = quitPlan.getStartDate();
        LocalDateTime now = LocalDateTime.now();
        
        if (quitDate.isAfter(now)) {
            log.warn("Quit date is in the future for user: {}", user.getUserId());
            return;
        }
        
        // Lấy tất cả daily summaries
        List<DailySummary> allDailySummaries = dailySummaryRepository.findByQuitPlan(quitPlan);
        
        // Tính tổng số điếu thuốc đã hút
        int totalCigarettesSmoked = allDailySummaries.stream()
                .mapToInt(summary -> summary.getTotalSmokedCount())
                .sum();
        
        // Tính số ngày KHÔNG hút thuốc (totalSmokedCount = 0)
        int nonSmokingDays = (int) allDailySummaries.stream()
                .filter(summary -> summary.getTotalSmokedCount() == 0)
                .count();
        
        // Tính số ngày hút thuốc
        int smokingDays = (int) allDailySummaries.stream()
                .filter(summary -> summary.getTotalSmokedCount() > 0)
                .count();
        
        log.info("User {} - Non-smoking days: {}, Smoking days: {}, Total cigarettes: {}", 
                user.getUserId(), nonSmokingDays, smokingDays, totalCigarettesSmoked);
        
        List<HealthMetric> userMetrics = healthMetricRepository.findByUserOrderByMetricType(user);
        
        for (HealthMetric metric : userMetrics) {
            double targetHours = metric.getMetricType().getTargetHours();
            
            // Tính giờ phục hồi từ số ngày không hút
            // Mỗi ngày không hút = 24 giờ phục hồi
            double recoveryHours = nonSmokingDays * 24.0;
            
            // Tính giờ bị mất do hút thuốc
            // 1 điếu = giảm 2 giờ phục hồi
            double lostHours = totalCigarettesSmoked * 2.0;
            
            // Tính giờ phục hồi thực tế (có thể âm nếu hút nhiều)
            double netRecoveryHours = Math.max(0, recoveryHours - lostHours);
            
            // Tính progress dựa trên giờ phục hồi thực tế
            double timeProgress = Math.min(100.0, (netRecoveryHours / targetHours) * 100.0);
            
            // Progress không thể âm
            double finalProgress = Math.max(0, timeProgress);
            
            // Lưu progress cũ để so sánh
            double oldProgress = metric.getCurrentProgress();
            boolean wasCompleted = metric.getIsCompleted();
            
            metric.setCurrentProgress(finalProgress);
            metric.setTimeRemainingHours(Math.max(0, (long) (targetHours - netRecoveryHours)));
            
            // Kiểm tra nếu đã hoàn thành
            if (finalProgress >= 100.0 && !metric.getIsCompleted()) {
                metric.setIsCompleted(true);
                metric.setAchievedDate(now);
                metric.setCurrentProgress(100.0);
                metric.setTimeRemainingHours(0L);
                
                log.info("Health metric completed for user {}: {} (non-smoking days: {}, cigarettes: {})", 
                        user.getUserId(), metric.getMetricType(), nonSmokingDays, totalCigarettesSmoked);
            }
            
            // Kiểm tra nếu đã hoàn thành trước đó nhưng bây giờ tụt xuống dưới 100%
            if (wasCompleted && finalProgress < 100.0) {
                metric.setIsCompleted(false);
                metric.setAchievedDate(null);
                metric.setHasRegressed(true);
                log.info("Health metric regressed for user {}: {} (progress: {} -> {})", 
                        user.getUserId(), metric.getMetricType(), oldProgress, finalProgress);
            }
            
            // Kiểm tra nếu đã từng tụt xuống và bây giờ đạt 100% trở lại
            if (metric.getHasRegressed() && finalProgress >= 100.0) {
                metric.setIsCompleted(true);
                metric.setAchievedDate(now);
                metric.setCurrentProgress(100.0);
                metric.setTimeRemainingHours(0L);
                metric.setHasRegressed(false); // Reset regression flag
                
                log.info("Health metric recovered and completed again for user {}: {}", 
                        user.getUserId(), metric.getMetricType());
            }
            
            // Cập nhật target date - tính từ quitDate + target hours
            LocalDateTime targetDate = quitDate.plusHours((long) targetHours);
            metric.setTargetDate(targetDate);
            
            healthMetricRepository.save(metric);
        }
        
        log.info("Health metrics progress updated for user: {} (non-smoking days: {}, cigarettes: {})", 
                user.getUserId(), nonSmokingDays, totalCigarettesSmoked);
    }
    
    /**
     * Lấy tất cả health metrics của user
     */
    @Transactional(readOnly = true)
    public List<HealthMetricDTO> getUserHealthMetrics(User user) {
        List<HealthMetric> metrics = healthMetricRepository.findByUserOrderByMetricType(user);
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy health overview của user
     */
    @Transactional(readOnly = true)
    public HealthOverviewDTO getHealthOverview(User user) {
        // Cập nhật tiến độ trước khi lấy overview
        updateHealthMetricsProgress(user);
        
        Long totalMetrics = healthMetricRepository.countTotalMetricsByUser(user);
        Long completedMetrics = healthMetricRepository.countCompletedMetricsByUser(user);
        Long inProgressMetrics = totalMetrics - completedMetrics;
        
        double overallProgress = totalMetrics > 0 ? (completedMetrics.doubleValue() / totalMetrics.doubleValue()) * 100.0 : 0.0;
        
        List<HealthMetricDTO> topProgressMetrics = healthMetricRepository.findTopProgressMetricsByUser(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        List<HealthMetricDTO> upcomingMilestones = healthMetricRepository.findIncompleteMetricsByUserOrderByTargetHours(user)
                .stream()
                .limit(5)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        List<HealthMetricDTO> recentAchievements = healthMetricRepository.findCompletedMetricsByUserOrderByAchievedDate(user)
                .stream()
                .limit(3)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // Tính thời gian bỏ thuốc
        Member member = user.getMember();
        Optional<QuitPlan> currentPlan = member != null ? 
            quitPlanRepository.findActiveQuitPlanByMember(member) : Optional.empty();
        Long daysSinceQuit = 0L;
        Long hoursSinceQuit = 0L;
        String nextMilestone = "Start your quit journey";
        
        if (currentPlan.isPresent()) {
            QuitPlan quitPlan = currentPlan.get();
            LocalDateTime quitDate = quitPlan.getStartDate();
            LocalDateTime now = LocalDateTime.now();
            
            if (quitDate.isBefore(now)) {
                Duration duration = Duration.between(quitDate, now);
                hoursSinceQuit = duration.toHours();
                daysSinceQuit = hoursSinceQuit / 24;
                
                if (!upcomingMilestones.isEmpty()) {
                    nextMilestone = upcomingMilestones.get(0).getDisplayName();
                }
            }
        }
        
        return HealthOverviewDTO.builder()
                .totalMetrics(totalMetrics)
                .completedMetrics(completedMetrics)
                .inProgressMetrics(inProgressMetrics)
                .overallProgress(overallProgress)
                .topProgressMetrics(topProgressMetrics)
                .upcomingMilestones(upcomingMilestones)
                .recentAchievements(recentAchievements)
                .nextMilestone(nextMilestone)
                .daysSinceQuit(daysSinceQuit)
                .hoursSinceQuit(hoursSinceQuit)
                .build();
    }
    
    /**
     * Lấy health metric theo type
     */
    @Transactional(readOnly = true)
    public Optional<HealthMetricDTO> getHealthMetricByType(User user, HealthMetricType metricType) {
        return healthMetricRepository.findByUserAndMetricType(user, metricType)
                .map(this::convertToDTO);
    }
    
    /**
     * Convert entity to DTO
     */
    private HealthMetricDTO convertToDTO(HealthMetric healthMetric) {
        String timeRemainingFormatted = formatTimeRemaining(healthMetric.getTimeRemainingHours());
        
        return HealthMetricDTO.builder()
                .id(healthMetric.getId())
                .userId(healthMetric.getUser().getUserId())
                .metricType(healthMetric.getMetricType())
                .displayName(healthMetric.getMetricType().getDisplayName())
                .description(healthMetric.getMetricType().getDescription())
                .currentProgress(healthMetric.getCurrentProgress())
                .targetDate(healthMetric.getTargetDate())
                .achievedDate(healthMetric.getAchievedDate())
                .isCompleted(healthMetric.getIsCompleted())
                .timeRemainingHours(healthMetric.getTimeRemainingHours())
                .timeRemainingFormatted(timeRemainingFormatted)
                .hasRegressed(healthMetric.getHasRegressed())
                .createdAt(healthMetric.getCreatedAt())
                .updatedAt(healthMetric.getUpdatedAt())
                .build();
    }
    
    /**
     * Format time remaining thành string dễ đọc
     */
    private String formatTimeRemaining(Long hours) {
        if (hours == null || hours <= 0) {
            return "Đã hoàn thành";
        }
        
        long days = hours / 24;
        long remainingHours = hours % 24;
        
        if (days > 0) {
            if (remainingHours > 0) {
                return String.format("%d ngày và %d giờ", days, remainingHours);
            } else {
                return String.format("%d ngày", days);
            }
        } else {
            return String.format("%d giờ", remainingHours);
        }
    }
} 