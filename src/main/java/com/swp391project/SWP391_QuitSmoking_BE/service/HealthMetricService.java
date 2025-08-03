package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.health.HealthMetricDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.health.HealthOverviewDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.HealthMetric;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.HealthMetricRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HealthMetricService {

    private static final double MAX_PROGRESS = 100.0;
    private static final double MIN_PROGRESS = 0.0;
    private static final double PENALTY_PER_CIGARETTE = 60.0; // 1 điếu = 60 phút
    private static final double COMPLETION_THRESHOLD = 100.0;

    private final HealthMetricRepository healthMetricRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final DailySummaryRepository dailySummaryRepository;

    /**
     * Khởi tạo health metrics cho user mới
     */
    public void initializeHealthMetrics(User user) {
        log.info("Initializing health metrics for user: {}", user.getUserId());
        
        // Lấy quit date từ active quit plan (sử dụng created_at thay vì start_date)
        Optional<QuitPlan> activeQuitPlan = quitPlanRepository.findActiveQuitPlanByMember(user.getMember());
        if (activeQuitPlan.isEmpty()) {
            log.warn("No active quit plan found for user: {}", user.getUserId());
            return;
        }
        
        LocalDateTime quitDate = activeQuitPlan.get().getCreatedAt();
        log.info("Using quit plan created_at as quit date: {}", quitDate);
        
        // Define health metrics with target minutes (matching database constraint)
        String[][] healthMetrics = {
            {"PULSE_RATE", "Sau 20 phút, nhịp tim của bạn sẽ trở về bình thường", "20"},
            {"OXYGEN_LEVELS", "Sau 8 giờ, nồng độ oxy trong máu sẽ trở về bình thường", "480"},
            {"CARBON_MONOXIDE", "Sau 24 giờ, carbon monoxide sẽ được loại bỏ khỏi cơ thể", "1440"},
            {"NICOTINE_EXPELLED", "Sau 72 giờ, nicotine sẽ được loại bỏ khỏi cơ thể", "4320"},
            {"TASTE_SMELL", "Sau 48 giờ, vị giác và khứu giác của bạn sẽ được cải thiện", "2880"},
            {"BREATHING", "Sau 72 giờ, hơi thở của bạn sẽ dễ dàng hơn", "4320"},
            {"ENERGY_LEVELS", "Sau 72 giờ, mức năng lượng của bạn sẽ tăng", "4320"},
            {"BAD_BREATH_GONE", "Sau 72 giờ, hơi thở hôi sẽ biến mất", "4320"},
            {"GUMS_TEETH", "Sau 1 tuần, nướu và răng sẽ được cải thiện", "10080"},
            {"TEETH_BRIGHTNESS", "Sau 2 tuần, răng sẽ sáng hơn", "20160"},
            {"CIRCULATION", "Sau 2 tuần, tuần hoàn máu sẽ được cải thiện", "20160"},
            {"GUM_TEXTURE", "Sau 1 tháng, kết cấu nướu sẽ được cải thiện", "43200"},
            {"IMMUNITY_LUNG_FUNCTION", "Sau 1 tháng, chức năng phổi và miễn dịch sẽ được cải thiện", "43200"},
            {"HEART_DISEASE_RISK", "Sau 1 năm, nguy cơ bệnh tim sẽ giảm một nửa", "525600"},
            {"LUNG_CANCER_RISK", "Sau 10 năm, nguy cơ ung thư phổi sẽ giảm một nửa", "5256000"},
            {"HEART_ATTACK_RISK", "Sau 1 năm, nguy cơ đau tim sẽ giảm một nửa", "525600"}
        };
        
        for (String[] metric : healthMetrics) {
            try {
                String metricType = metric[0];
                String description = metric[1];
                double targetMinutes = Double.parseDouble(metric[2]);
                
                if (!healthMetricRepository.existsByUserAndMetricType(user, metricType)) {
                    LocalDateTime targetDate = quitDate.plusMinutes((long) targetMinutes);
                    LocalDateTime now = LocalDateTime.now();
                    
                    HealthMetric healthMetric = new HealthMetric();
                    healthMetric.setUser(user);
                    healthMetric.setMetricType(metricType);
                    healthMetric.setCurrentProgress(0.0);
                    healthMetric.setTargetDate(targetDate);
                    healthMetric.setIsCompleted(false);
                    healthMetric.setHasRegressed(false);
                    healthMetric.setDescription(description);
                    healthMetric.setTimeRemainingHours(targetMinutes / 60.0);
                    // createdAt và updatedAt sẽ được set tự động bởi @PrePersist
                    
                    healthMetricRepository.save(healthMetric);
                    log.debug("Created health metric: {} for user: {}", metricType, user.getUserId());
                }
            } catch (Exception e) {
                log.error("Error creating health metric {} for user {}: {}", metric[0], user.getUserId(), e.getMessage(), e);
                throw e;
            }
        }
        log.info("Health metrics initialized for user: {}", user.getUserId());
    }

    /**
     * Cập nhật progress cho tất cả health metrics của user
     */
    public void updateHealthMetricsProgress(User user) {
        log.info("Updating health metrics progress for user: {}", user.getUserId());
        
        Optional<QuitPlan> activeQuitPlan = quitPlanRepository.findActiveQuitPlanByMember(user.getMember());
        if (activeQuitPlan.isEmpty()) {
            log.warn("No active quit plan found for user: {}", user.getUserId());
            return;
        }
        
        QuitPlan quitPlan = activeQuitPlan.get();
        LocalDateTime quitDate = quitPlan.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        
        List<DailySummary> dailySummaries = dailySummaryRepository.findByQuitPlan(quitPlan);
        
        // Tính tổng số điếu thuốc đã hút từ khi bắt đầu quit plan
        int totalCigarettesSmoked = dailySummaries.stream()
                .mapToInt(DailySummary::getTotalSmokedCount)
                .sum();
        
        // Tính penalty: 1 điếu = 60 phút
        double penaltyMinutes = totalCigarettesSmoked * PENALTY_PER_CIGARETTE;
        
        log.info("=== PENALTY DEBUG ===");
        log.info("User: {}, Total cigarettes: {}, Penalty minutes: {}", 
                user.getUserId(), totalCigarettesSmoked, penaltyMinutes);
        log.info("Daily summaries count: {}", dailySummaries.size());
        dailySummaries.forEach(summary -> 
            log.info("  Total smoked: {}", summary.getTotalSmokedCount())
        );
        log.info("=== END PENALTY DEBUG ===");
        
        List<HealthMetric> metrics = healthMetricRepository.findByUserOrderByMetricType(user);
        
        // XỬ LÝ COMPLETED METRICS TRƯỚC (nếu có penalty)
        if (totalCigarettesSmoked > 0) {
            handleCompletedMetricsWithPenalty(metrics, quitDate, now, penaltyMinutes, totalCigarettesSmoked, dailySummaries);
        }
        
        // Sau đó cập nhật tất cả metrics
        for (HealthMetric metric : metrics) {
            updateSingleMetric(metric, quitDate, now, penaltyMinutes, totalCigarettesSmoked);
        }
        
        healthMetricRepository.saveAll(metrics);
        log.info("Updated {} health metrics for user: {}", metrics.size(), user.getUserId());
    }

    /**
     * Xử lý completed metrics có penalty
     */
    private void handleCompletedMetricsWithPenalty(List<HealthMetric> metrics, LocalDateTime quitDate, 
                                                 LocalDateTime now, double penaltyMinutes, int totalCigarettesSmoked,
                                                 List<DailySummary> dailySummaries) {
        log.info("Handling completed metrics with penalty");
        
        if (dailySummaries.isEmpty()) {
            log.warn("No daily summaries found, skipping completed metrics handling");
            return;
        }
        
        // Lấy latest daily summary
        DailySummary latestDailySummary = dailySummaries.stream()
                .max(Comparator.comparing(DailySummary::getCreatedAt))
                .orElse(dailySummaries.get(dailySummaries.size() - 1));
        
        log.info("Latest daily summary: {}", latestDailySummary.getCreatedAt());
        
        // Xử lý từng completed metric
        for (HealthMetric metric : metrics) {
            if (metric.getIsCompleted()) {
                String metricType = metric.getMetricType();
                double targetMinutes = getTargetMinutesFromMetricType(metricType);
                
                log.info("Processing completed metric: {}", metricType);
                
                // Giới hạn penalty không vượt quá target time
                double limitedPenalty = Math.min(penaltyMinutes, targetMinutes);
                
                // Target date mới = latest daily summary created_at + penalty
                LocalDateTime newTargetDate = latestDailySummary.getCreatedAt().plusMinutes((long) limitedPenalty);
                
                // Progress giảm = progress cũ - (penalty/target_time)
                double penaltyEffect = (limitedPenalty / targetMinutes) * 100.0;
                double newProgress = Math.max(0.0, 100.0 - penaltyEffect);
                
                // Cập nhật metric
                metric.setTargetDate(newTargetDate);
                metric.setCurrentProgress(newProgress);
                metric.setHasRegressed(true);
                
                // Set completed = false nếu progress < 100%
                if (newProgress < COMPLETION_THRESHOLD) {
                    metric.setIsCompleted(false);
                    metric.setAchievedDate(null);
                    log.info("Set {} to incomplete, progress: {:.2f}%", metricType, newProgress);
                } else {
                    log.info("Keep {} as completed, progress: {:.2f}%", metricType, newProgress);
                }
                
                log.info("Updated {}: targetDate={}, progress={:.2f}%, completed={}", 
                        metricType, newTargetDate, newProgress, metric.getIsCompleted());
            }
        }
    }

    /**
     * Cập nhật single metric với logic đơn giản hơn
     */
    private void updateSingleMetric(HealthMetric metric, LocalDateTime quitDate, LocalDateTime now, 
                                   double penaltyMinutes, int totalCigarettesSmoked) {
        
        String metricType = metric.getMetricType();
        double targetMinutes = getTargetMinutesFromMetricType(metricType);
        
        log.info("Updating metric: {}, target: {} minutes, penalty: {} minutes", 
                metricType, targetMinutes, penaltyMinutes);
        
        LocalDateTime targetDate;
        double progress;
        
        // Kiểm tra nếu metric đã completed trước đó
        boolean wasCompleted = metric.getIsCompleted();
        
        // Nếu metric đã completed và có penalty - XỬ LÝ ĐẦU TIÊN
        if (wasCompleted && totalCigarettesSmoked > 0) {
            log.info("COMPLETED METRIC WITH PENALTY: applying penalty logic");
            
            // Lấy latest daily summary để tính target date mới
            Optional<QuitPlan> activeQuitPlan = quitPlanRepository.findActiveQuitPlanByMember(metric.getUser().getMember());
            if (activeQuitPlan.isPresent()) {
                List<DailySummary> dailySummaries = dailySummaryRepository.findByQuitPlan(activeQuitPlan.get());
                
                if (!dailySummaries.isEmpty()) {
                    // Lấy latest daily summary
                    DailySummary latestDailySummary = dailySummaries.stream()
                            .max(Comparator.comparing(DailySummary::getCreatedAt))
                            .orElse(dailySummaries.get(dailySummaries.size() - 1));
                    
                    // Giới hạn penalty không vượt quá target time
                    double limitedPenalty = Math.min(penaltyMinutes, targetMinutes);
                    
                    // Target date mới = latest daily summary created_at + penalty
                    targetDate = latestDailySummary.getCreatedAt().plusMinutes((long) limitedPenalty);
                    
                    // Progress giảm = progress cũ - (penalty/target_time)
                    double penaltyEffect = (limitedPenalty / targetMinutes) * 100.0;
                    progress = Math.max(0.0, 100.0 - penaltyEffect);
                    
                    log.info("COMPLETED PENALTY: latest daily summary={}, limited penalty={} minutes", 
                            latestDailySummary.getCreatedAt(), limitedPenalty);
                    log.info("COMPLETED PENALTY: new target date={}, progress reduced from 100% to {:.2f}%", 
                            targetDate, progress);
                } else {
                    // Fallback nếu không có daily summary
                    targetDate = now.plusMinutes((long) targetMinutes);
                    progress = 0.0;
                    log.warn("COMPLETED PENALTY: no daily summaries found, using fallback");
                }
            } else {
                // Fallback nếu không có quit plan
                targetDate = now.plusMinutes((long) targetMinutes);
                progress = 0.0;
                log.warn("COMPLETED PENALTY: no active quit plan found, using fallback");
            }
        } else if (wasCompleted && totalCigarettesSmoked == 0) {
            // Nếu metric đã completed và không có penalty: giữ nguyên
            progress = 100.0;
            targetDate = metric.getTargetDate(); // Giữ nguyên target date cũ
            log.info("KEEP COMPLETED: metric was already completed, keeping 100%");
        } else {
            // Logic cho metrics chưa completed hoặc đã bị penalty trước đó
            log.info("INCOMPLETE METRIC OR PREVIOUSLY PENALIZED: applying logic");
            
            // Kiểm tra nếu metric có hasRegressed = true (đã bị penalty trước đó)
            if (metric.getHasRegressed() && totalCigarettesSmoked > 0) {
                log.info("PREVIOUSLY PENALIZED METRIC: calculating progress from target date");
                
                // Sử dụng target date hiện tại để tính progress
                targetDate = metric.getTargetDate();
                
                // Tính progress dựa trên target date (không phải quit date)
                if (now.isAfter(targetDate) || now.isEqual(targetDate)) {
                    progress = 100.0; // Đã hoàn thành
                } else {
                    // Tính progress dựa trên thời gian đã trôi qua so với target date
                    long elapsedMinutes = ChronoUnit.MINUTES.between(quitDate, now);
                    long totalRequiredMinutes = ChronoUnit.MINUTES.between(quitDate, targetDate);
                    progress = (elapsedMinutes / (double) totalRequiredMinutes) * 100.0;
                }
                
                log.info("PREVIOUSLY PENALIZED: targetDate={}, progress={:.2f}%", targetDate, progress);
            } else {
                // Logic cho metrics chưa completed (giữ nguyên logic cũ)
                log.info("INCOMPLETE METRIC: applying original logic");
                
                // Tính target date gốc (không có penalty)
                LocalDateTime originalTargetDate = quitDate.plusMinutes((long) targetMinutes);
                
                if (penaltyMinutes >= targetMinutes) {
                    // RESET CASE: Penalty >= target time
                    targetDate = now.plusMinutes((long) targetMinutes);
                    progress = 0.0; // Reset về 0%
                    log.info("RESET CASE: penalty >= target, reset to 0%");
                } else {
                    // PENALTY CASE: Penalty < target time
                    long remainingMinutes = ChronoUnit.MINUTES.between(now, originalTargetDate);
                    long newRemainingMinutes = remainingMinutes + (long) penaltyMinutes;
                    newRemainingMinutes = Math.min(newRemainingMinutes, (long) targetMinutes);
                    long penaltyEffectMinutes = newRemainingMinutes - remainingMinutes;
                    targetDate = originalTargetDate.plusMinutes(penaltyEffectMinutes);
                    
                    // Tính progress từ quit date
                    long elapsedMinutesFromQuit = ChronoUnit.MINUTES.between(quitDate, now);
                    progress = (elapsedMinutesFromQuit / targetMinutes) * 100.0;
                    
                    log.info("PENALTY CASE: remaining={} minutes, new remaining={} minutes, penalty effect={} minutes", 
                            remainingMinutes, newRemainingMinutes, penaltyEffectMinutes);
                    log.info("PENALTY CASE: progress: {:.2f}%", progress);
                }
            }
        }
        
        log.info("Target date calculation: final={}", targetDate);
        log.info("Progress calculation: progress = {:.2f}%", progress);
        
        // Cập nhật target date
        metric.setTargetDate(targetDate);
        
        // Cập nhật progress và trạng thái
        metric.setCurrentProgress(Math.max(MIN_PROGRESS, Math.min(MAX_PROGRESS, progress)));
        metric.setHasRegressed(totalCigarettesSmoked > 0);
        
        // QUAN TRỌNG: Set completed status dựa trên progress
        if (progress >= COMPLETION_THRESHOLD) {
            metric.setIsCompleted(true);
            metric.setCurrentProgress(MAX_PROGRESS);
            metric.setAchievedDate(now);
        } else {
            // ĐẢM BẢO: Nếu progress < 100% thì set completed = false
            metric.setIsCompleted(false);
            metric.setAchievedDate(null);
        }
        
        updateTimeRemainingHours(metric, now);
        
        log.info("Final result: targetDate={}, progress={:.2f}%, completed={}", 
                targetDate, progress, metric.getIsCompleted());
    }



    /**
     * Tính progress từ target date - SỬA LẠI LOGIC
     * Progress = ((target_time - time_remaining) / target_time) * 100%
     */
    private double calculateProgressFromTargetDate(LocalDateTime targetDate, LocalDateTime now, double targetMinutes) {
        if (now.isAfter(targetDate) || now.isEqual(targetDate)) {
            return MAX_PROGRESS;
        }
        
        // Tính thời gian còn lại (time_remaining)
        long remainingMinutes = ChronoUnit.MINUTES.between(now, targetDate);
        
        // Tính thời gian đã trôi qua
        double elapsedMinutes = targetMinutes - remainingMinutes;
        
        // Progress = (elapsed_time / target_time) * 100%
        double progress = (elapsedMinutes / targetMinutes) * 100.0;
        
        return Math.max(MIN_PROGRESS, Math.min(MAX_PROGRESS, progress));
    }

    /**
     * Cập nhật time remaining hours với độ chính xác phút (không tính giây)
     */
    private void updateTimeRemainingHours(HealthMetric metric, LocalDateTime now) {
        if (metric.getIsCompleted()) {
            metric.setTimeRemainingHours(0.0);
        } else {
            // Tính bằng phút để làm tròn xuống, không tính giây
            long remainingMinutes = ChronoUnit.MINUTES.between(now, metric.getTargetDate());
            double remainingHours = Math.max(0.0, remainingMinutes / 60.0); // Chuyển phút thành giờ
            metric.setTimeRemainingHours(remainingHours);
        }
    }

    /**
     * Lấy target minutes từ metric type
     */
    private double getTargetMinutesFromMetricType(String metricType) {
        return switch (metricType) {
            case "PULSE_RATE" -> 20.0;
            case "OXYGEN_LEVELS" -> 480.0;
            case "CARBON_MONOXIDE" -> 1440.0;
            case "NICOTINE_EXPELLED", "BREATHING", "ENERGY_LEVELS", "BAD_BREATH_GONE" -> 4320.0;
            case "TASTE_SMELL" -> 2880.0;
            case "GUMS_TEETH" -> 10080.0;
            case "TEETH_BRIGHTNESS", "CIRCULATION" -> 20160.0;
            case "GUM_TEXTURE", "IMMUNITY_LUNG_FUNCTION" -> 43200.0;
            case "HEART_DISEASE_RISK", "HEART_ATTACK_RISK" -> 525600.0;
            case "LUNG_CANCER_RISK" -> 5256000.0;
            default -> 1440.0;
        };
    }

    /**
     * Lấy health overview cho user
     */
    @Transactional
    public HealthOverviewDTO getHealthOverview(User user) {
        // Cập nhật progress trước khi đọc
        updateHealthMetricsProgress(user);
        
        List<HealthMetric> metrics = healthMetricRepository.findByUserOrderByMetricType(user);
        
        if (metrics.isEmpty()) {
            // Tạo sample data nếu không có metrics
            return createSampleHealthOverview();
        }
        
        Long totalMetrics = (long) metrics.size();
        Long completedMetrics = healthMetricRepository.countCompletedByUser(user);
        Long regressedMetrics = (long) metrics.stream().filter(HealthMetric::getHasRegressed).count();
        Long inProgressMetrics = totalMetrics - completedMetrics;
        
        double overallProgress = metrics.stream()
                .mapToDouble(HealthMetric::getCurrentProgress)
                .average()
                .orElse(0.0);
        
        List<HealthMetricDTO> metricDTOs = metrics.stream()
                .map(this::convertToDTO)
                .toList();
        
        // Tính toán các metrics khác
        List<HealthMetricDTO> topProgressMetrics = metrics.stream()
                .filter(m -> m.getCurrentProgress() > 0 && !m.getIsCompleted())
                .sorted((a, b) -> Double.compare(b.getCurrentProgress(), a.getCurrentProgress()))
                .limit(5)
                .map(this::convertToDTO)
                .toList();
        
        List<HealthMetricDTO> upcomingMilestones = metrics.stream()
                .filter(m -> !m.getIsCompleted() && m.getCurrentProgress() < 100)
                .sorted((a, b) -> Double.compare(a.getCurrentProgress(), b.getCurrentProgress()))
                .limit(3)
                .map(this::convertToDTO)
                .toList();
        
        List<HealthMetricDTO> recentAchievements = metrics.stream()
                .filter(HealthMetric::getIsCompleted)
                .sorted((a, b) -> {
                    if (a.getAchievedDate() == null && b.getAchievedDate() == null) return 0;
                    if (a.getAchievedDate() == null) return 1;
                    if (b.getAchievedDate() == null) return -1;
                    return b.getAchievedDate().compareTo(a.getAchievedDate());
                })
                .limit(3)
                .map(this::convertToDTO)
                .toList();
        
        // Tính next milestone
        String nextMilestone = upcomingMilestones.isEmpty() ? "Tất cả đã hoàn thành!" : 
                upcomingMilestones.get(0).getDisplayName();
        
        // Tính thời gian từ quit date (giả sử 2 ngày)
        Long daysSinceQuit = 2L;
        Long hoursSinceQuit = 48L;
        
        return HealthOverviewDTO.builder()
                .totalMetrics(totalMetrics)
                .completedMetrics(completedMetrics)
                .inProgressMetrics(inProgressMetrics)
                .regressedMetrics(regressedMetrics)
                .overallProgress(overallProgress)
                .topProgressMetrics(topProgressMetrics)
                .upcomingMilestones(upcomingMilestones)
                .recentAchievements(recentAchievements)
                .nextMilestone(nextMilestone)
                .daysSinceQuit(daysSinceQuit)
                .hoursSinceQuit(hoursSinceQuit)
                .metrics(metricDTOs)
                .build();
    }

    /**
     * Lấy tất cả health metrics của user
     */
    @Transactional
    public List<HealthMetricDTO> getUserHealthMetrics(User user) {
        // Cập nhật progress trước khi đọc
        updateHealthMetricsProgress(user);
        
        List<HealthMetric> metrics = healthMetricRepository.findByUserOrderByMetricType(user);
        
        if (metrics.isEmpty()) {
            return createSampleHealthMetrics();
        }
        
        return metrics.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Convert HealthMetric to DTO
     */
    private HealthMetricDTO convertToDTO(HealthMetric healthMetric) {
        return HealthMetricDTO.builder()
                .id(healthMetric.getId().toString())
                .metricType(healthMetric.getMetricType())
                .currentProgress(healthMetric.getCurrentProgress())
                .isCompleted(healthMetric.getIsCompleted())
                .hasRegressed(healthMetric.getHasRegressed())
                .description(healthMetric.getDescription())
                .targetDate(healthMetric.getTargetDate())
                .achievedDate(healthMetric.getAchievedDate())
                .timeRemainingHours(healthMetric.getTimeRemainingHours())
                .timeRemainingFormatted(formatTimeRemaining(healthMetric.getTimeRemainingHours()))
                .displayName(getDisplayNameFromMetricType(healthMetric.getMetricType()))
                .build();
    }

    /**
     * Format time remaining với giây (nhưng giây sẽ luôn là 00 vì đã làm tròn)
     */
    private String formatTimeRemaining(Double hours) {
        if (hours == null || hours <= 0) {
            return "Hoàn thành";
        }
        
        // Chuyển giờ thành phút (đã được làm tròn từ updateTimeRemainingHours)
        long totalMinutes = (long) (hours * 60);
        long days = totalMinutes / 1440; // 1440 = 24 * 60
        long remainingMinutes = totalMinutes % 1440;
        long hoursRemaining = remainingMinutes / 60;
        long minutesRemaining = remainingMinutes % 60;
        
        // Giây luôn là 00 vì đã làm tròn xuống đến phút
        long secondsRemaining = 0;
        
        if (days > 0) {
            return String.format("%d Ngày %d Giờ %d Phút %d Giây", days, hoursRemaining, minutesRemaining, secondsRemaining);
        } else if (hoursRemaining > 0) {
            return String.format("%d Giờ %d Phút %d Giây", hoursRemaining, minutesRemaining, secondsRemaining);
        } else if (minutesRemaining > 0) {
            return String.format("%d Phút %d Giây", minutesRemaining, secondsRemaining);
        } else {
            return String.format("%d Giây", secondsRemaining);
        }
    }

    /**
     * Lấy display name từ metric type
     */
    private String getDisplayNameFromMetricType(String metricType) {
        return switch (metricType) {
            case "PULSE_RATE" -> "Nhịp tim";
            case "OXYGEN_LEVELS" -> "Nồng độ oxy";
            case "CARBON_MONOXIDE" -> "Carbon monoxide";
            case "NICOTINE_EXPELLED" -> "Nicotine";
            case "TASTE_SMELL" -> "Vị giác & Khứu giác";
            case "BREATHING" -> "Hơi thở";
            case "ENERGY_LEVELS" -> "Năng lượng";
            case "BAD_BREATH_GONE" -> "Hơi thở hôi";
            case "GUMS_TEETH" -> "Nướu & Răng";
            case "TEETH_BRIGHTNESS" -> "Độ sáng răng";
            case "CIRCULATION" -> "Tuần hoàn máu";
            case "GUM_TEXTURE" -> "Kết cấu nướu";
            case "IMMUNITY_LUNG_FUNCTION" -> "Miễn dịch & Phổi";
            case "HEART_DISEASE_RISK" -> "Nguy cơ bệnh tim";
            case "LUNG_CANCER_RISK" -> "Nguy cơ ung thư phổi";
            case "HEART_ATTACK_RISK" -> "Nguy cơ đau tim";
            default -> metricType;
        };
    }

    /**
     * Tạo sample health overview
     */
    private HealthOverviewDTO createSampleHealthOverview() {
        List<HealthMetricDTO> sampleMetrics = createSampleHealthMetrics();
        
        return HealthOverviewDTO.builder()
                .totalMetrics((long) sampleMetrics.size())
                .completedMetrics(0L)
                .inProgressMetrics((long) sampleMetrics.size())
                .regressedMetrics(0L)
                .overallProgress(0.0)
                .topProgressMetrics(sampleMetrics.stream().limit(5).toList())
                .upcomingMilestones(sampleMetrics.stream().limit(3).toList())
                .recentAchievements(List.of())
                .nextMilestone("Bắt đầu hành trình bỏ thuốc")
                .daysSinceQuit(0L)
                .hoursSinceQuit(0L)
                .metrics(sampleMetrics)
                .build();
    }

    /**
     * Tạo sample health metrics
     */
    public List<HealthMetricDTO> createSampleHealthMetrics() {
        // Sử dụng thời gian hiện tại làm quit date cho sample data
        LocalDateTime quitDate = LocalDateTime.now();
        
        return List.of(
            createSampleMetric("PULSE_RATE", quitDate, 20, 0.0, false),
            createSampleMetric("OXYGEN_LEVELS", quitDate, 480, 0.0, false),
            createSampleMetric("CARBON_MONOXIDE", quitDate, 1440, 0.0, false),
            createSampleMetric("NICOTINE_EXPELLED", quitDate, 4320, 0.0, false),
            createSampleMetric("TASTE_SMELL", quitDate, 2880, 0.0, false),
            createSampleMetric("BREATHING", quitDate, 4320, 0.0, false),
            createSampleMetric("ENERGY_LEVELS", quitDate, 4320, 0.0, false),
            createSampleMetric("BAD_BREATH_GONE", quitDate, 4320, 0.0, false),
            createSampleMetric("GUMS_TEETH", quitDate, 10080, 0.0, false),
            createSampleMetric("TEETH_BRIGHTNESS", quitDate, 20160, 0.0, false),
            createSampleMetric("CIRCULATION", quitDate, 20160, 0.0, false),
            createSampleMetric("GUM_TEXTURE", quitDate, 43200, 0.0, false),
            createSampleMetric("IMMUNITY_LUNG_FUNCTION", quitDate, 43200, 0.0, false),
            createSampleMetric("HEART_DISEASE_RISK", quitDate, 525600, 0.0, false),
            createSampleMetric("LUNG_CANCER_RISK", quitDate, 5256000, 0.0, false),
            createSampleMetric("HEART_ATTACK_RISK", quitDate, 525600, 0.0, false)
        );
    }

    /**
     * Tạo sample metric
     */
    private HealthMetricDTO createSampleMetric(String metricType, LocalDateTime quitDate, long targetMinutes, 
                                              double progress, boolean isCompleted) {
        LocalDateTime targetDate = quitDate.plusMinutes(targetMinutes);
        double remainingHours = Math.max(0.0, targetMinutes / 60.0);
        
        return HealthMetricDTO.builder()
                .id(java.util.UUID.randomUUID().toString())
                .metricType(metricType)
                .currentProgress(progress)
                .isCompleted(isCompleted)
                .hasRegressed(false)
                .description(getDescriptionFromMetricType(metricType))
                .targetDate(targetDate)
                .achievedDate(isCompleted ? quitDate.plusMinutes(targetMinutes) : null)
                .timeRemainingHours(remainingHours)
                .timeRemainingFormatted(formatTimeRemaining(remainingHours))
                .displayName(getDisplayNameFromMetricType(metricType))
                .build();
    }

    /**
     * Lấy description từ metric type
     */
    private String getDescriptionFromMetricType(String metricType) {
        return switch (metricType) {
            case "PULSE_RATE" -> "Sau 20 phút, nhịp tim của bạn sẽ trở về bình thường";
            case "OXYGEN_LEVELS" -> "Sau 8 giờ, nồng độ oxy trong máu sẽ trở về bình thường";
            case "CARBON_MONOXIDE" -> "Sau 24 giờ, carbon monoxide sẽ được loại bỏ khỏi cơ thể";
            case "NICOTINE_EXPELLED" -> "Sau 72 giờ, nicotine sẽ được loại bỏ khỏi cơ thể";
            case "TASTE_SMELL" -> "Sau 48 giờ, vị giác và khứu giác của bạn sẽ được cải thiện";
            case "BREATHING" -> "Sau 72 giờ, hơi thở của bạn sẽ dễ dàng hơn";
            case "ENERGY_LEVELS" -> "Sau 72 giờ, mức năng lượng của bạn sẽ tăng";
            case "BAD_BREATH_GONE" -> "Sau 72 giờ, hơi thở hôi sẽ biến mất";
            case "GUMS_TEETH" -> "Sau 1 tuần, nướu và răng sẽ được cải thiện";
            case "TEETH_BRIGHTNESS" -> "Sau 2 tuần, răng sẽ sáng hơn";
            case "CIRCULATION" -> "Sau 2 tuần, tuần hoàn máu sẽ được cải thiện";
            case "GUM_TEXTURE" -> "Sau 1 tháng, kết cấu nướu sẽ được cải thiện";
            case "IMMUNITY_LUNG_FUNCTION" -> "Sau 1 tháng, chức năng phổi và miễn dịch sẽ được cải thiện";
            case "HEART_DISEASE_RISK" -> "Sau 1 năm, nguy cơ bệnh tim sẽ giảm một nửa";
            case "LUNG_CANCER_RISK" -> "Sau 10 năm, nguy cơ ung thư phổi sẽ giảm một nửa";
            case "HEART_ATTACK_RISK" -> "Sau 1 năm, nguy cơ đau tim sẽ giảm một nửa";
            default -> "Cải thiện sức khỏe";
        };
    }

    /**
     * Test method để kiểm tra logic penalty và progress calculation
     */
    public void testPenaltyAndProgressLogic() {
        log.info("=== TESTING PENALTY AND PROGRESS LOGIC ===");
        
        try {
            LocalDateTime quitDate = LocalDateTime.now().minusDays(1); // Quit 1 ngày trước
            LocalDateTime now = LocalDateTime.now();
            
            log.info("Quit date: {}", quitDate);
            log.info("Current time: {}", now);
            
            // Test case 1: PULSE_RATE (20 phút) - không có penalty
            testMetricLogic("PULSE_RATE", quitDate, now, 0, "Test 1: PULSE_RATE không penalty");
            
            // Test case 2: PULSE_RATE (20 phút) - có 1 điếu thuốc (60 phút penalty)
            testMetricLogic("PULSE_RATE", quitDate, now, 1, "Test 2: PULSE_RATE có 1 điếu penalty");
            
            // Test case 3: PULSE_RATE (20 phút) - có 5 điếu thuốc (300 phút penalty > 20 phút target)
            testMetricLogic("PULSE_RATE", quitDate, now, 5, "Test 3: PULSE_RATE có 5 điếu (reset case)");
            
            // Test case 4: OXYGEN_LEVELS (480 phút) - có 2 điếu thuốc (120 phút penalty < 480 phút target)
            testMetricLogic("OXYGEN_LEVELS", quitDate, now, 2, "Test 4: OXYGEN_LEVELS có 2 điếu penalty");
            
            log.info("=== END TESTING ===");
        } catch (Exception e) {
            log.error("Error in testPenaltyAndProgressLogic: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void testMetricLogic(String metricType, LocalDateTime quitDate, LocalDateTime now, int cigarettes, String testName) {
        try {
            double targetMinutes = getTargetMinutesFromMetricType(metricType);
            double penaltyMinutes = cigarettes * PENALTY_PER_CIGARETTE;
            
            // Tính target date ban đầu
            LocalDateTime originalTargetDate = quitDate.plusMinutes((long) targetMinutes);
            
            // Tính progress ban đầu
            double originalProgress = calculateProgressFromTargetDate(originalTargetDate, now, targetMinutes);
            
            log.info("{}", testName);
            log.info("  Metric: {} ({} phút)", metricType, targetMinutes);
            log.info("  Cigarettes: {} ({} phút penalty)", cigarettes, penaltyMinutes);
            log.info("  Original target date: {}", originalTargetDate);
            log.info("  Original progress: {:.2f}%", originalProgress);
            
            if (penaltyMinutes >= targetMinutes) {
                // Reset case
                LocalDateTime newTargetDate = now.plusMinutes((long) targetMinutes);
                double newProgress = 0.0; // Reset về 0%
                log.info("  RESET CASE: penalty >= target");
                log.info("  New target date: {}", newTargetDate);
                log.info("  New progress: {:.2f}% (reset to 0%)", newProgress);
            } else {
                // Penalty case
                LocalDateTime newTargetDate = originalTargetDate.plusMinutes((long) penaltyMinutes);
                double newProgress = calculateProgressFromTargetDate(newTargetDate, now, targetMinutes);
                log.info("  PENALTY CASE: penalty < target");
                log.info("  New target date: {}", newTargetDate);
                log.info("  New progress: {:.2f}%", newProgress);
            }
            
            log.info("  ---");
        } catch (Exception e) {
            log.error("Error in testMetricLogic for {}: {}", testName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Test method để kiểm tra tính toán thời gian bỏ thuốc
     */
    public String testQuitTimeCalculation() {
        try {
            // Giả sử có một user test
            // Trong thực tế, bạn cần lấy user thực từ database
            log.info("=== TESTING QUIT TIME CALCULATION ===");
            
            // Tạo quit date giả lập (1 giờ trước)
            LocalDateTime quitDate = LocalDateTime.now().minusHours(1);
            LocalDateTime now = LocalDateTime.now();
            
            long totalMinutes = ChronoUnit.MINUTES.between(quitDate, now);
            long daysSinceQuit = totalMinutes / 1440; // 1440 = 24 * 60
            long hoursSinceQuit = totalMinutes / 60; // Tổng giờ
            
            String result = String.format(
                "Quit date: %s\nCurrent time: %s\nTotal minutes: %d\nDays: %d\nHours: %d",
                quitDate, now, totalMinutes, daysSinceQuit, hoursSinceQuit
            );
            
            log.info("Quit time calculation result: {}", result);
            log.info("=== END QUIT TIME TEST ===");
            
            return result;
        } catch (Exception e) {
            log.error("Error in testQuitTimeCalculation: {}", e.getMessage(), e);
            throw e;
        }
    }
} 