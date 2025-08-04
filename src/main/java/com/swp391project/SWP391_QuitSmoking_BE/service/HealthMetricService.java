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
            {"PULSE_RATE", "Sau 22 giờ, nhịp tim của bạn sẽ trở về bình thường", "1320"},
            {"OXYGEN_LEVELS", "Sau 1 ngày 5 giờ, nồng độ oxy trong máu sẽ trở về bình thường", "1740"},
            {"CARBON_MONOXIDE", "Sau 1 ngày 21 giờ, carbon monoxide sẽ được loại bỏ khỏi cơ thể", "2700"},
            {"NICOTINE_EXPELLED", "Sau 2 ngày 21 giờ, nicotine sẽ được loại bỏ khỏi cơ thể", "4140"},
            {"TASTE_SMELL", "Sau 3 ngày 9 giờ, vị giác và khứu giác của bạn sẽ được cải thiện", "4860"},
            {"BREATHING", "Sau 3 ngày 21 giờ, hơi thở của bạn sẽ dễ dàng hơn", "5580"},
            {"ENERGY_LEVELS", "Sau 4 ngày 21 giờ, mức năng lượng của bạn sẽ tăng", "7020"},
            {"BAD_BREATH_GONE", "Sau 7 ngày 21 giờ, hơi thở hôi sẽ biến mất", "11340"},
            {"GUMS_TEETH", "Sau 14 ngày 21 giờ, nướu và răng sẽ được cải thiện", "21420"},
            {"TEETH_BRIGHTNESS", "Sau 14 ngày 21 giờ, răng sẽ sáng hơn", "21420"},
            {"CIRCULATION", "Sau 2 tháng 29 ngày, tuần hoàn máu sẽ được cải thiện", "128160"},
            {"GUM_TEXTURE", "Sau 2 tháng 29 ngày, kết cấu nướu sẽ được cải thiện", "128160"},
            {"IMMUNITY_LUNG_FUNCTION", "Sau 4 tháng 18 ngày, chức năng phổi và miễn dịch sẽ được cải thiện", "198720"},
            {"HEART_DISEASE_RISK", "Sau 1 năm, nguy cơ bệnh tim sẽ giảm một nửa", "525600"},
            {"LUNG_CANCER_RISK", "Sau 10 năm, nguy cơ ung thư phổi sẽ giảm một nửa", "5256000"},
            {"HEART_ATTACK_RISK", "Sau 15 năm, nguy cơ đau tim sẽ giảm một nửa", "7884000"}
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
        
        // CẬP NHẬT TẤT CẢ METRICS VỚI PENALTY (bao gồm cả completed và non-completed)
        for (HealthMetric metric : metrics) {
            updateSingleMetric(metric, quitDate, now, penaltyMinutes, totalCigarettesSmoked);
        }
        
        healthMetricRepository.saveAll(metrics);
        log.info("Updated {} health metrics for user: {}", metrics.size(), user.getUserId());
    }



    /**
     * Cập nhật single metric với penalty logic thống nhất
     */
    private void updateSingleMetric(HealthMetric metric, LocalDateTime quitDate, LocalDateTime now, 
                                   double penaltyMinutes, int totalCigarettesSmoked) {
        
        String metricType = metric.getMetricType();
        double targetMinutes = getTargetMinutesFromMetricType(metricType);
        
        log.info("Updating metric: {}, target: {} minutes, penalty: {} minutes", 
                metricType, targetMinutes, penaltyMinutes);
        
        LocalDateTime targetDate;
        double progress;
        
        // CÔNG THỨC THỐNG NHẤT: Progress dựa trên thời gian thực tế + penalty
        long elapsedMinutes = ChronoUnit.MINUTES.between(quitDate, now);
        double effectiveElapsedMinutes = Math.max(0, elapsedMinutes - penaltyMinutes);
        
        if (effectiveElapsedMinutes >= targetMinutes) {
            // COMPLETED: Đã đủ thời gian
            progress = 100.0;
            targetDate = quitDate.plusMinutes((long) targetMinutes);
            log.info("COMPLETED: effective elapsed time >= target time");
        } else if (effectiveElapsedMinutes <= 0) {
            // NOT STARTED: Penalty >= elapsed time
            progress = 0.0;
            targetDate = quitDate.plusMinutes((long) targetMinutes);
            log.info("NOT STARTED: penalty >= elapsed time, reset to 0%");
        } else {
            // IN PROGRESS: Tính progress theo tỷ lệ thời gian
            progress = (effectiveElapsedMinutes / targetMinutes) * 100.0;
            targetDate = quitDate.plusMinutes((long) targetMinutes);
            log.info("IN PROGRESS: progress = {:.2f}%", progress);
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
            case "PULSE_RATE" -> 1320.0; // 22 giờ
            case "OXYGEN_LEVELS" -> 1740.0; // 1 ngày 5 giờ
            case "CARBON_MONOXIDE" -> 2700.0; // 1 ngày 21 giờ
            case "NICOTINE_EXPELLED" -> 4140.0; // 2 ngày 21 giờ
            case "TASTE_SMELL" -> 4860.0; // 3 ngày 9 giờ
            case "BREATHING" -> 5580.0; // 3 ngày 21 giờ
            case "ENERGY_LEVELS" -> 7020.0; // 4 ngày 21 giờ
            case "BAD_BREATH_GONE" -> 11340.0; // 7 ngày 21 giờ
            case "GUMS_TEETH", "TEETH_BRIGHTNESS" -> 21420.0; // 14 ngày 21 giờ
            case "CIRCULATION", "GUM_TEXTURE" -> 128160.0; // 2 tháng 29 ngày
            case "IMMUNITY_LUNG_FUNCTION" -> 198720.0; // 4 tháng 18 ngày
            case "HEART_DISEASE_RISK" -> 525600.0; // 1 năm
            case "LUNG_CANCER_RISK" -> 5256000.0; // 10 năm
            case "HEART_ATTACK_RISK" -> 7884000.0; // 15 năm
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
            // Trả về empty overview nếu không có metrics
            return HealthOverviewDTO.builder()
                    .totalMetrics(0L)
                    .completedMetrics(0L)
                    .inProgressMetrics(0L)
                    .regressedMetrics(0L)
                    .overallProgress(0.0)
                    .topProgressMetrics(List.of())
                    .upcomingMilestones(List.of())
                    .recentAchievements(List.of())
                    .nextMilestone("Chưa có dữ liệu")
                    .daysSinceQuit(0L)
                    .hoursSinceQuit(0L)
                    .metrics(List.of())
                    .build();
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
            return List.of();
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




} 