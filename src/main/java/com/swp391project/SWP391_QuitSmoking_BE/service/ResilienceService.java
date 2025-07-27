package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResilienceService {
    
    private final QuitPlanRepository quitPlanRepository;
    private final MemberRepository memberRepository;
    private final DailySummaryRepository dailySummaryRepository;

    /**
     * 1. QuitPlan History Tracking
     * Lấy lịch sử tất cả QuitPlan của member với phân tích chi tiết
     */
    public List<QuitPlanHistoryDTO> getQuitPlanHistory(UUID memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return List.of();
        }
        
        List<QuitPlan> allQuitPlans = quitPlanRepository.findByMemberOrderByCreatedAtDesc(member);
        
        return allQuitPlans.stream().map(plan -> {
            // Tính completion rate cho từng plan
            long totalDays = ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), plan.getGoalDate());
            long completedDays = plan.getStatus() == QuitPlanStatus.IN_PROGRESS ? 
                ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), LocalDate.now()) :
                ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), plan.getGoalDate());
            
            double completionRate = totalDays > 0 ? (double) completedDays / totalDays * 100 : 0;
            
            // Tính streak days từ daily summaries
            List<DailySummary> summaries = dailySummaryRepository.findByQuitPlan_QuitPlanId(plan.getQuitPlanId());
            int maxStreak = calculateMaxStreak(summaries);
            
            return new QuitPlanHistoryDTO(
                plan.getQuitPlanId(),
                plan.getCreatedAt(),
                plan.getStartDate(),
                plan.getGoalDate(),
                plan.getStatus(),
                plan.getInitialSmokingAmount(),
                completionRate,
                maxStreak,
                (int) completedDays,
                (int) totalDays
            );
        }).collect(Collectors.toList());
    }

    /**
     * 2. Failure Detection Logic
     * Phân tích pattern thất bại để hiểu nguyên nhân
     */
    public FailureAnalysisDTO detectFailurePattern(UUID memberId) {
        List<QuitPlanHistoryDTO> history = getQuitPlanHistory(memberId);
        
        if (history.isEmpty()) {
            return new FailureAnalysisDTO(0, 0, 0, "No quit attempts found", List.of());
        }
        
        int totalAttempts = history.size();
        int failedAttempts = (int) history.stream().filter(h -> h.getStatus() == QuitPlanStatus.FAILED).count();
        int completedAttempts = (int) history.stream().filter(h -> h.getStatus() == QuitPlanStatus.COMPLETED).count();
        
        // Phân tích failure points (ngày thường fail)
        List<String> commonFailurePoints = history.stream()
            .filter(h -> h.getStatus() == QuitPlanStatus.FAILED)
            .map(h -> {
                if (h.getCompletedDays() <= 3) return "Early failure (1-3 days)";
                else if (h.getCompletedDays() <= 7) return "Week 1 challenge (4-7 days)";
                else if (h.getCompletedDays() <= 21) return "Three week hurdle (8-21 days)";
                else if (h.getCompletedDays() <= 90) return "Long-term challenge (22-90 days)";
                else return "Advanced stage failure (90+ days)";
            })
            .distinct()
            .collect(Collectors.toList());
        
        String primaryFailureReason = commonFailurePoints.isEmpty() ? 
            "No specific pattern detected" : 
            commonFailurePoints.getFirst();
        
        return new FailureAnalysisDTO(
            totalAttempts,
            failedAttempts,
            completedAttempts,
            primaryFailureReason,
            commonFailurePoints
        );
    }

    /**
     * 3. Comeback Achievement System
     * Tính toán metrics cho comeback achievements
     */
    public ComebackMetricsDTO calculateComebackMetrics(UUID memberId) {
        List<QuitPlanHistoryDTO> history = getQuitPlanHistory(memberId);
        
        if (history.size() <= 1) {
            return new ComebackMetricsDTO(0, 0, 0, false, 0);
        }
        
        // Tính time between attempts (trung bình số ngày giữa các lần thử)
        long totalTimeBetween = 0;
        for (int i = 1; i < history.size(); i++) {
            long daysBetween = ChronoUnit.DAYS.between(
                history.get(i).getCreatedAt().toLocalDate(),
                history.get(i-1).getCreatedAt().toLocalDate()
            );
            totalTimeBetween += daysBetween;
        }
        
        double averageTimeBetween = history.size() > 1 ? 
            (double) totalTimeBetween / (history.size() - 1) : 0;
        
        // Check improvement trend
        boolean showsImprovement = false;
        if (history.size() >= 2) {
            QuitPlanHistoryDTO latest = history.get(0);
            QuitPlanHistoryDTO previous = history.get(1);
            showsImprovement = latest.getCompletionRate() > previous.getCompletionRate() ||
                              latest.getMaxStreakDays() > previous.getMaxStreakDays();
        }
        
        // Calculate total comeback attempts
        int comebackCount = history.size() - 1;
        
        // Calculate persistence score
        double persistenceScore = history.stream()
            .mapToDouble(QuitPlanHistoryDTO::getCompletionRate)
            .average()
            .orElse(0);
        
        return new ComebackMetricsDTO(
            comebackCount,
            (int) averageTimeBetween,
            persistenceScore,
            showsImprovement,
            history.stream().mapToInt(QuitPlanHistoryDTO::getMaxStreakDays).max().orElse(0)
        );
    }

    /**
     * 4. Progress Comparison Metrics
     * So sánh tiến độ với lần thử trước đó
     */
    public ProgressComparisonDTO compareProgressWithPrevious(UUID memberId) {
        List<QuitPlanHistoryDTO> history = getQuitPlanHistory(memberId);
        
        if (history.size() < 2) {
            return new ProgressComparisonDTO(
                false, 0, 0, 0, "No previous attempt to compare", false
            );
        }
        
        QuitPlanHistoryDTO current = history.get(0);
        QuitPlanHistoryDTO previous = history.get(1);
        
        double completionImprovement = current.getCompletionRate() - previous.getCompletionRate();
        int streakImprovement = current.getMaxStreakDays() - previous.getMaxStreakDays();
        int durationImprovement = current.getCompletedDays() - previous.getCompletedDays();
        
        boolean overallImprovement = completionImprovement > 0 || streakImprovement > 0 || durationImprovement > 0;
        
        String comparisonSummary;
        if (overallImprovement) {
            comparisonSummary = "You're doing better than your previous attempt!";
        } else if (completionImprovement == 0 && streakImprovement == 0 && durationImprovement == 0) {
            comparisonSummary = "Similar performance to previous attempt.";
        } else {
            comparisonSummary = "Keep trying - every attempt teaches you something!";
        }
        
        boolean learningCurvePositive = history.size() >= 3 && 
            history.get(0).getCompletionRate() > history.get(2).getCompletionRate();
        
        return new ProgressComparisonDTO(
            overallImprovement,
            completionImprovement,
            streakImprovement,
            durationImprovement,
            comparisonSummary,
            learningCurvePositive
        );
    }

    // Helper methods
    private int calculateMaxStreak(List<DailySummary> summaries) {
        if (summaries.isEmpty()) return 0;
        
        int maxStreak = 0;
        int currentStreak = 0;
        
        // Sort by date
        summaries.sort((a, b) -> a.getTrackDate().compareTo(b.getTrackDate()));
        
        for (DailySummary summary : summaries) {
            if (summary.isGoalAchievedToday()) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }
        
        return maxStreak;
    }

    // DTOs
    @Data
    @AllArgsConstructor
    public static class QuitPlanHistoryDTO {
        private Integer quitPlanId;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime startDate;
        private java.time.LocalDate goalDate;
        private QuitPlanStatus status;
        private int initialSmokingAmount;
        private double completionRate;
        private int maxStreakDays;
        private int completedDays;
        private int totalDays;
    }

    @Data
    @AllArgsConstructor
    public static class FailureAnalysisDTO {
        private int totalAttempts;
        private int failedAttempts;
        private int completedAttempts;
        private String primaryFailureReason;
        private List<String> commonFailurePoints;
    }

    @Data
    @AllArgsConstructor
    public static class ComebackMetricsDTO {
        private int comebackCount;
        private int averageTimeBetweenAttempts;
        private double persistenceScore;
        private boolean showsImprovement;
        private int bestStreakEver;
    }

    @Data
    @AllArgsConstructor
    public static class ProgressComparisonDTO {
        private boolean overallImprovement;
        private double completionImprovement;
        private int streakImprovement;
        private int durationImprovement;
        private String comparisonSummary;
        private boolean learningCurvePositive;
    }
}
