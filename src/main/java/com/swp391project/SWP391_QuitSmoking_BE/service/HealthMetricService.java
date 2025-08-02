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
import java.time.ZoneOffset;
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
        
        log.info("User {} - Total cigarettes smoked: {}, quitDate: {}, now: {}", 
                user.getUserId(), totalCigarettesSmoked, quitDate, now);
        
        List<HealthMetric> userMetrics = healthMetricRepository.findByUserOrderByMetricType(user);
        
        for (HealthMetric metric : userMetrics) {
            double targetHours = metric.getMetricType().getTargetHours();
            
            // Lưu trạng thái cũ để so sánh
            boolean wasCompleted = metric.getIsCompleted();
            
            log.debug("Processing metric {} for user {}: targetHours={}, currentProgress={}, isCompleted={}, hasRegressed={}", 
                    metric.getMetricType(), user.getUserId(), targetHours, metric.getCurrentProgress(), 
                    metric.getIsCompleted(), metric.getHasRegressed());
            
            // LOGIC PENALTY MỚI: 1 điếu = +1 giờ vào thời gian cần thiết
            LocalDateTime targetDate;
            double finalProgress;
            double timeRemainingHours;
            
            if (totalCigarettesSmoked > 0) {
                // Có hút thuốc: áp dụng penalty
                double penaltyHours = totalCigarettesSmoked * 1.0; // 1 điếu = 1 giờ
                
                if (metric.getIsCompleted() && !metric.getHasRegressed()) {
                    // Trường hợp 2: Health đã hoàn thành 100% - reset về now() + penalty (có giới hạn)
                    // Giới hạn penalty không vượt quá thời gian gốc
                    double maxPenaltyHours = targetHours;
                    double actualPenaltyHours = Math.min(penaltyHours, maxPenaltyHours);
                    
                    // Sử dụng minutes cho penalty < 1 giờ để tránh mất phần thập phân
                    if (actualPenaltyHours < 1.0) {
                        long penaltyMinutes = Math.round(actualPenaltyHours * 60.0);
                        targetDate = now.plusMinutes(penaltyMinutes);
                    } else {
                        targetDate = now.plusHours((long) actualPenaltyHours);
                    }
                    finalProgress = 0.0; // Reset về 0% để tính lại
                    metric.setIsCompleted(false);
                    metric.setAchievedDate(null);
                    metric.setHasRegressed(true);
                    
                    log.info("Health metric regressed due to smoking for user {}: {} (cigarettes: {}, penalty: {} hours, max allowed: {} hours)", 
                            user.getUserId(), metric.getMetricType(), totalCigarettesSmoked, actualPenaltyHours, maxPenaltyHours);
                } else {
                    // Trường hợp 1: Health chưa hoàn thành hoặc đã regressed
                    // Sử dụng minutes cho metric < 1 giờ để tránh mất phần thập phân
                    LocalDateTime originalTargetDate;
                    if (targetHours < 1.0) {
                        long targetMinutes = Math.round(targetHours * 60.0);
                        originalTargetDate = quitDate.plusMinutes(targetMinutes);
                    } else {
                        originalTargetDate = quitDate.plusHours((long) targetHours);
                    }
                    
                    // Giới hạn penalty không vượt quá thời gian gốc
                    double maxPenaltyHours = targetHours;
                    double actualPenaltyHours = Math.min(penaltyHours, maxPenaltyHours);
                    
                    // Tính targetDate mới với penalty
                    // Sử dụng minutes cho penalty < 1 giờ để tránh mất phần thập phân
                    if (actualPenaltyHours < 1.0) {
                        long penaltyMinutes = Math.round(actualPenaltyHours * 60.0);
                        targetDate = originalTargetDate.plusMinutes(penaltyMinutes);
                    } else {
                        targetDate = originalTargetDate.plusHours((long) actualPenaltyHours);
                    }
                    
                    // Tính progress dựa trên thời gian đã trôi qua so với targetDate mới
                    double timeProgress;
                    if (targetHours < 1.0) {
                        // Sử dụng minutes cho metric < 1 giờ
                        long minutesSinceQuit = Duration.between(quitDate, now).toMinutes();
                        double totalRequiredMinutes = (targetHours + actualPenaltyHours) * 60.0;
                        // Đảm bảo không vượt quá 100% do lỗi làm tròn
                        timeProgress = Math.min(100.0, Math.max(0.0, (minutesSinceQuit / totalRequiredMinutes) * 100.0));
                    } else {
                        // Sử dụng hours cho metric >= 1 giờ
                        long hoursSinceQuit = Duration.between(quitDate, now).toHours();
                        double totalRequiredHours = targetHours + actualPenaltyHours;
                        timeProgress = Math.min(100.0, Math.max(0.0, (hoursSinceQuit / totalRequiredHours) * 100.0));
                    }
                    finalProgress = Math.max(0, timeProgress);
                    
                    log.info("Health metric penalty applied for user {}: {} (cigarettes: {}, penalty: {} hours, progress: {}%)", 
                            user.getUserId(), metric.getMetricType(), totalCigarettesSmoked, actualPenaltyHours, finalProgress);
                    
                    log.debug("User {} - Metric {} with penalty: targetHours={}, timeProgress={}%, finalProgress={}%", 
                            user.getUserId(), metric.getMetricType(), targetHours, timeProgress, finalProgress);
                }
            } else {
                // Không hút thuốc: tính toán bình thường
                // Sử dụng minutes cho metric < 1 giờ để tránh mất phần thập phân
                if (targetHours < 1.0) {
                    long targetMinutes = Math.round(targetHours * 60.0);
                    targetDate = quitDate.plusMinutes(targetMinutes);
                } else {
                    targetDate = quitDate.plusHours((long) targetHours);
                }
                
                // Tính progress dựa trên thời gian đã trôi qua
                // Sử dụng minutes cho các metric ngắn (< 1 giờ) để tránh tính sai
                double timeProgress;
                if (targetHours < 1.0) {
                    // Sử dụng minutes cho metric < 1 giờ
                    long minutesSinceQuit = Duration.between(quitDate, now).toMinutes();
                    double targetMinutes = targetHours * 60.0;
                    // Đảm bảo không vượt quá 100% do lỗi làm tròn
                    timeProgress = Math.min(100.0, Math.max(0.0, (minutesSinceQuit / targetMinutes) * 100.0));
                } else {
                    // Sử dụng hours cho metric >= 1 giờ
                    long hoursSinceQuit = Duration.between(quitDate, now).toHours();
                    timeProgress = Math.min(100.0, Math.max(0.0, (hoursSinceQuit / targetHours) * 100.0));
                }
                finalProgress = Math.max(0, timeProgress);
                
                log.debug("User {} - Metric {}: targetHours={}, timeProgress={}%, finalProgress={}%", 
                        user.getUserId(), metric.getMetricType(), targetHours, timeProgress, finalProgress);
            }
            
            // Cập nhật targetDate và timeRemainingHours
            metric.setTargetDate(targetDate);
            
            // Tính timeRemainingHours với validation
            if (targetDate != null && now != null) {
                timeRemainingHours = Math.max(0, (targetDate.toInstant(ZoneOffset.UTC).toEpochMilli() - now.toInstant(ZoneOffset.UTC).toEpochMilli()) / (1000 * 60 * 60));
            } else {
                timeRemainingHours = 0.0;
                log.warn("Invalid targetDate or now for user {} metric {}: targetDate={}, now={}", 
                        user.getUserId(), metric.getMetricType(), targetDate, now);
            }
            
            metric.setTimeRemainingHours(timeRemainingHours);
            metric.setCurrentProgress(finalProgress);
            
            // Kiểm tra nếu đã hoàn thành (chỉ khi không có penalty)
            if (finalProgress >= 100.0 && !metric.getIsCompleted() && !metric.getHasRegressed()) {
                metric.setIsCompleted(true);
                metric.setAchievedDate(now);
                metric.setCurrentProgress(100.0); // Đảm bảo consistency
                metric.setTimeRemainingHours(0.0);
                
                log.info("Health metric completed for user {}: {} (cigarettes: {}, achieved at: {})", 
                        user.getUserId(), metric.getMetricType(), totalCigarettesSmoked, now);
            }
            
            // Kiểm tra nếu đã từng tụt xuống và bây giờ đạt 100% trở lại (chỉ khi không có penalty)
            if (metric.getHasRegressed() && finalProgress >= 100.0 && totalCigarettesSmoked == 0) {
                // CHỈ set isCompleted = true nếu thực sự đã đạt được mục tiêu
                // Không set achievedDate = now vì có thể gây nhầm lẫn
                // Chỉ set khi thực sự đạt được lần đầu và chưa có achievedDate
                if (metric.getAchievedDate() == null) {
                    // Chỉ set achievedDate nếu thời gian hiện tại đã vượt quá thời gian hoàn thành dự kiến
                    LocalDateTime expectedCompletionTime;
                    if (targetHours < 1.0) {
                        long targetMinutes = Math.round(targetHours * 60.0);
                        expectedCompletionTime = quitDate.plusMinutes(targetMinutes);
                    } else {
                        expectedCompletionTime = quitDate.plusHours((long) targetHours);
                    }
                    
                    // Chỉ set achievedDate nếu thời gian hiện tại đã vượt quá thời gian hoàn thành dự kiến
                    if (now.isAfter(expectedCompletionTime)) {
                        metric.setAchievedDate(expectedCompletionTime); // Set đúng thời gian hoàn thành, không phải now()
                    }
                }
                
                metric.setIsCompleted(true);
                metric.setCurrentProgress(100.0);
                metric.setTimeRemainingHours(0.0);
                metric.setHasRegressed(false); // Reset regression flag
                
                log.info("Health metric recovered and completed again for user {}: {} (achieved at: {})", 
                        user.getUserId(), metric.getMetricType(), metric.getAchievedDate());
            }
            
            healthMetricRepository.save(metric);
            
            log.debug("Final result for metric {}: progress={}%, completed={}, regressed={}, targetDate={}, timeRemaining={} hours", 
                    metric.getMetricType(), metric.getCurrentProgress(), metric.getIsCompleted(), 
                    metric.getHasRegressed(), metric.getTargetDate(), metric.getTimeRemainingHours());
        }
        
        log.info("Health metrics progress updated for user: {} (cigarettes: {}, penalty applied: {} hours)", 
                user.getUserId(), totalCigarettesSmoked, totalCigarettesSmoked > 0 ? totalCigarettesSmoked : 0);
    }

    /**
     * Trigger update health metrics khi có daily summary mới
     * Method này sẽ được gọi từ DailySummaryService
     */
    public void triggerHealthMetricsUpdate(User user) {
        log.info("Triggering health metrics update for user: {}", user.getUserId());
        updateHealthMetricsProgress(user);
    }
    
    /**
     * Lấy tất cả health metrics của user
     */
    @Transactional
    public List<HealthMetricDTO> getUserHealthMetrics(User user) {
        List<HealthMetric> metrics = healthMetricRepository.findByUserOrderByMetricType(user);
        
        // Debug logging để kiểm tra data
        log.info("Retrieved {} health metrics for user: {}", metrics.size(), user.getUserId());
        for (HealthMetric metric : metrics) {
            log.debug("Metric {}: progress={}%, completed={}, regressed={}, targetDate={}", 
                    metric.getMetricType(), 
                    metric.getCurrentProgress(), 
                    metric.getIsCompleted(), 
                    metric.getHasRegressed(),
                    metric.getTargetDate());
        }
        
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
    private String formatTimeRemaining(Double hours) {
        if (hours == null || hours <= 0) {
            return "Đã hoàn thành";
        }
        
        long days = (long) (hours / 24);
        double remainingHours = hours % 24;
        
        if (days > 0) {
            if (remainingHours > 0) {
                return String.format("%d ngày và %.1f giờ", days, remainingHours);
            } else {
                return String.format("%d ngày", days);
            }
        } else {
            // Hiển thị chi tiết hơn cho thời gian < 24 giờ
            if (remainingHours >= 1) {
                long wholeHours = (long) remainingHours;
                double minutes = (remainingHours - wholeHours) * 60;
                if (minutes > 0) {
                    return String.format("%d giờ %.0f phút", wholeHours, minutes);
                } else {
                    return String.format("%d giờ", wholeHours);
                }
            } else {
                double minutes = remainingHours * 60;
                if (minutes >= 1) {
                    return String.format("%.0f phút", minutes);
                } else {
                    double seconds = minutes * 60;
                    return String.format("%.0f giây", seconds);
                }
            }
        }
    }
} 