package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal.WeeklyGoalRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal.WeeklyGoalResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal.WeeklyGoalSummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyGoalService {

    private final WeeklyGoalRepository weeklyGoalRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final DailySummaryRepository dailySummaryRepository;
    private final AchievementTriggerService achievementTriggerService;
    private final NotificationService notificationService;

    /**
     * Tạo weekly goal cho thành viên
     */
    @Transactional
    public WeeklyGoalResponse setWeeklyGoal(UUID memberId, WeeklyGoalRequest request) {
        log.info("Setting weekly goal for member: {}", memberId);

        // Lấy active quit plan
        QuitPlan currentPlan = getCurrentPlan(memberId);
        
        // Tính toán tuần hiện tại
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Kiểm tra xem đã có goal cho tuần này chưa
        Optional<WeeklyGoal> existingGoal = weeklyGoalRepository.findCurrentWeeklyGoal(currentPlan, now);
        if (existingGoal.isPresent()) {
            throw new IllegalStateException("Đã có mục tiêu cho tuần này. Vui lòng cập nhật thay vì tạo mới.");
        }

        // Validate và adjust targets based on historical data
        WeeklyGoalRequest adjustedRequest = validateAndAdjustTargets(currentPlan, request);

        // Tạo weekly goal
        WeeklyGoal weeklyGoal = WeeklyGoal.builder()
                .quitPlan(currentPlan)
                .weekStartDate(weekStart)
                .weekEndDate(weekEnd)
                .targetSmokedCount(adjustedRequest.getTargetSmokedCount())
                .targetCravingResistance(adjustedRequest.getTargetCravingResistance())
                .difficultyLevel(adjustedRequest.getDifficultyLevel())
                .goalType(adjustedRequest.getGoalType())
                .notes(adjustedRequest.getNotes())
                .rewardPoints(calculateRewardPoints(adjustedRequest.getDifficultyLevel()))
                .isAchieved(false)
                .progressPercentage(BigDecimal.ZERO)
                .actualSmokedCount(0)
                .actualCravingResistance(0)
                .createdAt(LocalDateTime.now())
                .build();

        WeeklyGoal savedGoal = weeklyGoalRepository.save(weeklyGoal);

        // Schedule progress check notifications
        scheduleProgressChecks(memberId, savedGoal);

        // Send notification
        Notification notification = new Notification();
        notification.setUserId(memberId);
        notification.setTitle("Mục tiêu tuần mới");
        notification.setContent("Bạn đã đặt mục tiêu thành công cho tuần này. Hãy cố gắng hoàn thành nhé!");
        notification.setNotificationType("WEEKLY_GOAL_SET");
        notificationService.createNotification(notification);

        log.info("Created weekly goal with ID: {} for member: {}", savedGoal.getWeeklyGoalId(), memberId);
        return WeeklyGoalResponse.fromEntity(savedGoal);
    }

    /**
     * Lấy thông tin weekly goal hiện tại
     */
    @Transactional(readOnly = true)
    public WeeklyGoalResponse getCurrentWeeklyGoal(UUID memberId) {
        LocalDate now = LocalDate.now();
        Optional<WeeklyGoal> currentGoal = weeklyGoalRepository.findCurrentWeeklyGoalByMemberId(memberId, now);
        
        return currentGoal.map(WeeklyGoalResponse::fromEntity).orElse(null);
    }

    /**
     * Lấy tổng quan về weekly goals
     */
    @Transactional(readOnly = true)
    public WeeklyGoalSummary getWeeklyGoalSummary(UUID memberId) {
        log.info("Getting weekly goal summary for member: {}", memberId);

        List<WeeklyGoal> allGoals = weeklyGoalRepository.findByMemberIdOrderByWeekStartDateDesc(memberId);
        
        if (allGoals.isEmpty()) {
            return createEmptySummary(memberId);
        }

        // Calculate statistics
        int totalGoals = allGoals.size();
        int achievedGoals = (int) allGoals.stream().filter(WeeklyGoal::getIsAchieved).count();
        int failedGoals = (int) allGoals.stream()
                .filter(goal -> !goal.getIsAchieved() && LocalDate.now().isAfter(goal.getWeekEndDate()))
                .count();
        int inProgressGoals = totalGoals - achievedGoals - failedGoals;

        BigDecimal achievementRate = totalGoals > 0 
                ? BigDecimal.valueOf(achievedGoals * 100.0 / totalGoals).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Get current week goal
        WeeklyGoalResponse currentWeekGoal = getCurrentWeeklyGoal(memberId);

        // Get recent goals
        List<WeeklyGoalResponse> recentGoals = allGoals.stream()
                .limit(5)
                .map(WeeklyGoalResponse::fromEntity)
                .collect(Collectors.toList());

        // Calculate trends and recommendations
        String progressTrend = calculateProgressTrend(allGoals);
        WeeklyGoalRequest suggestions = generateAdaptiveGoalSuggestions(memberId, allGoals);

        return WeeklyGoalSummary.builder()
                .totalGoals(totalGoals)
                .achievedGoals(achievedGoals)
                .failedGoals(failedGoals)
                .inProgressGoals(inProgressGoals)
                .achievementRate(achievementRate)
                .currentStreak(calculateCurrentStreak(allGoals))
                .bestStreak(calculateBestStreak(allGoals))
                .totalRewardPoints(calculateTotalRewardPoints(allGoals))
                .currentWeekGoal(currentWeekGoal)
                .recentGoals(recentGoals)
                .averageProgress(calculateAverageProgress(allGoals))
                .progressTrend(progressTrend)
                .suggestedSmokedTarget(suggestions.getTargetSmokedCount())
                .suggestedCravingTarget(suggestions.getTargetCravingResistance())
                .recommendedDifficulty(suggestions.getDifficultyLevel().name())
                .motivationalMessage(generateMotivationalMessage(achievementRate, progressTrend))
                .smokingReductionRate(calculateSmokingReductionRate(allGoals))
                .cravingResistanceRate(calculateCravingResistanceRate(allGoals))
                .build();
    }

    /**
     * Cập nhật progress cho tất cả weekly goals đang active
     */
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    @Transactional
    public void updateWeeklyGoalProgress() {
        log.info("Starting weekly goal progress update");

        LocalDate today = LocalDate.now();
        List<WeeklyGoal> activeGoals = weeklyGoalRepository.findActiveWeeklyGoals(today);

        for (WeeklyGoal goal : activeGoals) {
            try {
                updateSingleGoalProgress(goal);
            } catch (Exception e) {
                log.error("Error updating progress for weekly goal {}: {}", goal.getWeeklyGoalId(), e.getMessage());
            }
        }

        log.info("Completed weekly goal progress update for {} goals", activeGoals.size());
    }

    /**
     * Update progress cho một goal cụ thể
     */
    @Transactional
    public void updateSingleGoalProgress(WeeklyGoal goal) {
        // Lấy dữ liệu từ daily summaries trong tuần
        List<DailySummary> weekSummaries = dailySummaryRepository
                .findByQuitPlanAndTrackDateBetween(
                        goal.getQuitPlan(),
                        goal.getWeekStartDate(),
                        goal.getWeekEndDate()
                );

        // Tính tổng số thuốc đã hút và cravings đã chống chọi
        int totalSmoked = weekSummaries.stream()
                .mapToInt(DailySummary::getTotalSmokedCount)
                .sum();

        int totalCravingResisted = weekSummaries.stream()
                .mapToInt(DailySummary::getTrackedCravingCount)
                .sum();

        // Cập nhật actual values
        goal.setActualSmokedCount(totalSmoked);
        goal.setActualCravingResistance(totalCravingResisted);
        goal.setUpdatedAt(LocalDateTime.now());

        // Cập nhật progress
        goal.updateProgress();

        weeklyGoalRepository.save(goal);

        // Kiểm tra achievement
        if (goal.getIsAchieved() && goal.getCompletedAt() != null) {
            handleGoalAchievement(goal);
        }

        log.debug("Updated progress for weekly goal {}: {}%", 
                goal.getWeeklyGoalId(), goal.getProgressPercentage());
    }

    /**
     * Xử lý khi goal được hoàn thành
     */
    private void handleGoalAchievement(WeeklyGoal goal) {
        UUID memberId = goal.getQuitPlan().getMember().getMemberId();
        
        // Trigger achievement check
        achievementTriggerService.triggerAchievementCheck(memberId, "WEEKLY_GOAL_COMPLETED");

        // Send congratulations notification
        Notification notification = new Notification();
        notification.setUserId(memberId);
        notification.setTitle("🎉 Chúc mừng!");
        notification.setContent(String.format("Bạn đã hoàn thành mục tiêu tuần này và nhận được %d điểm thưởng!", 
                        goal.getRewardPoints()));
        notification.setNotificationType("WEEKLY_GOAL_ACHIEVED");
        notificationService.createNotification(notification);

        log.info("Weekly goal {} achieved by member {}", goal.getWeeklyGoalId(), memberId);
    }

    // Private helper methods

    private QuitPlan getCurrentPlan(UUID memberId) {
        return quitPlanRepository.findActiveQuitPlanByMemberId(memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy kế hoạch cai thuốc đang hoạt động cho thành viên: " + memberId));
    }

    private WeeklyGoalRequest validateAndAdjustTargets(QuitPlan quitPlan, WeeklyGoalRequest request) {
        // Lấy baseline từ daily summaries gần nhất
        List<DailySummary> recentSummaries = dailySummaryRepository
                .findTop7ByQuitPlanOrderByTrackDateDesc(quitPlan);

        if (recentSummaries.isEmpty()) {
            return request; // Không có dữ liệu lịch sử, giữ nguyên request
        }

        double avgWeeklySmoked = recentSummaries.stream()
                .mapToInt(DailySummary::getTotalSmokedCount)
                .average()
                .orElse(0.0) * 7; // Ước tính cho cả tuần

        // Điều chỉnh target dựa trên difficulty level
        Integer adjustedTarget = adjustTargetByDifficulty(
                (int) avgWeeklySmoked, 
                request.getTargetSmokedCount(), 
                request.getDifficultyLevel()
        );

        return new WeeklyGoalRequest(
                adjustedTarget,
                request.getTargetCravingResistance(),
                request.getDifficultyLevel(),
                request.getGoalType(),
                request.getNotes()
        );
    }

    private Integer adjustTargetByDifficulty(int baseline, int requestedTarget, WeeklyGoal.DifficultyLevel difficulty) {
        return switch (difficulty) {
            case EASY -> Math.max(requestedTarget, (int) (baseline * 0.8)); // 20% reduction max
            case NORMAL -> Math.max(requestedTarget, (int) (baseline * 0.6)); // 40% reduction max
            case HARD -> Math.max(requestedTarget, (int) (baseline * 0.4)); // 60% reduction max
            case EXTREME -> Math.max(requestedTarget, (int) (baseline * 0.2)); // 80% reduction max
            default -> requestedTarget;
        };
    }

    private Integer calculateRewardPoints(WeeklyGoal.DifficultyLevel difficulty) {
        return switch (difficulty) {
            case EASY -> 10;
            case NORMAL -> 20;
            case HARD -> 35;
            case EXTREME -> 50;
            default -> 20;
        };
    }

    private void scheduleProgressChecks(UUID memberId, WeeklyGoal goal) {
        // Logic để schedule progress check notifications
        // Có thể implement với Spring Task Scheduler hoặc Quartz
        log.info("Scheduled progress checks for weekly goal {} of member {}", 
                goal.getWeeklyGoalId(), memberId);
    }

    private WeeklyGoalSummary createEmptySummary(UUID memberId) {
        WeeklyGoalRequest suggestions = generateInitialGoalSuggestions(memberId);
        
        return WeeklyGoalSummary.builder()
                .totalGoals(0)
                .achievedGoals(0)
                .failedGoals(0)
                .inProgressGoals(0)
                .achievementRate(BigDecimal.ZERO)
                .currentStreak(0)
                .bestStreak(0)
                .totalRewardPoints(0)
                .currentWeekGoal(null)
                .recentGoals(Collections.emptyList())
                .averageProgress(BigDecimal.ZERO)
                .progressTrend("STABLE")
                .suggestedSmokedTarget(suggestions.getTargetSmokedCount())
                .suggestedCravingTarget(suggestions.getTargetCravingResistance())
                .recommendedDifficulty(suggestions.getDifficultyLevel().name())
                .motivationalMessage("Hãy bắt đầu với mục tiêu đầu tiên của bạn!")
                .smokingReductionRate(BigDecimal.ZERO)
                .cravingResistanceRate(BigDecimal.ZERO)
                .build();
    }

    private WeeklyGoalRequest generateInitialGoalSuggestions(UUID memberId) {
        // Lấy dữ liệu từ quit plan để suggest initial goals
        QuitPlan currentPlan = getCurrentPlan(memberId);
        
        // Suggest conservative initial targets
        int suggestedSmoked = Math.max(1, (int) (currentPlan.getInitialSmokingAmount() * 0.7));
        
        return new WeeklyGoalRequest(
                suggestedSmoked,
                5, // 5 cravings to resist per week
                WeeklyGoal.DifficultyLevel.EASY,
                WeeklyGoal.GoalType.REDUCTION,
                "Mục tiêu đầu tiên - hãy bắt đầu từ từ"
        );
    }

    private WeeklyGoalRequest generateAdaptiveGoalSuggestions(UUID memberId, List<WeeklyGoal> goals) {
        if (goals.isEmpty()) {
            return generateInitialGoalSuggestions(memberId);
        }

        // Phân tích performance gần nhất
        List<WeeklyGoal> recent = goals.stream().limit(3).toList();
        double avgAchievementRate = recent.stream()
                .filter(WeeklyGoal::getIsAchieved)
                .count() * 100.0 / recent.size();

        WeeklyGoal lastGoal = goals.getFirst();
        
        // Adaptive suggestions based on performance
        WeeklyGoal.DifficultyLevel suggestedDifficulty;
        int targetAdjustment;

        if (avgAchievementRate >= 80) {
            // Performing well, increase difficulty
            suggestedDifficulty = getNextDifficultyLevel(lastGoal.getDifficultyLevel());
            targetAdjustment = -2; // Reduce smoking target
        } else if (avgAchievementRate >= 50) {
            // Moderate performance, maintain level
            suggestedDifficulty = lastGoal.getDifficultyLevel();
            targetAdjustment = -1;
        } else {
            // Struggling, make it easier
            suggestedDifficulty = getPreviousDifficultyLevel(lastGoal.getDifficultyLevel());
            targetAdjustment = 1; // Increase smoking target (make it easier)
        }

        int suggestedSmoked = Math.max(0, lastGoal.getTargetSmokedCount() + targetAdjustment);
        int suggestedCraving = Math.max(3, lastGoal.getTargetCravingResistance());

        return new WeeklyGoalRequest(
                suggestedSmoked,
                suggestedCraving,
                suggestedDifficulty,
                WeeklyGoal.GoalType.COMBINED,
                "Mục tiêu được điều chỉnh dựa trên hiệu suất của bạn"
        );
    }

    private WeeklyGoal.DifficultyLevel getNextDifficultyLevel(WeeklyGoal.DifficultyLevel current) {
        return switch (current) {
            case EASY -> WeeklyGoal.DifficultyLevel.NORMAL;
            case NORMAL -> WeeklyGoal.DifficultyLevel.HARD;
            case HARD -> WeeklyGoal.DifficultyLevel.EXTREME;
            default -> current;
        };
    }

    private WeeklyGoal.DifficultyLevel getPreviousDifficultyLevel(WeeklyGoal.DifficultyLevel current) {
        return switch (current) {
            case EXTREME -> WeeklyGoal.DifficultyLevel.HARD;
            case HARD -> WeeklyGoal.DifficultyLevel.NORMAL;
            case NORMAL -> WeeklyGoal.DifficultyLevel.EASY;
            default -> current;
        };
    }

    // Additional helper methods for calculations
    private String calculateProgressTrend(List<WeeklyGoal> goals) {
        if (goals.size() < 3) return "STABLE";
        
        List<WeeklyGoal> recent = goals.stream().limit(3).toList();
        double avgRecentProgress = recent.stream()
                .mapToDouble(g -> g.getProgressPercentage().doubleValue())
                .average().orElse(0.0);
                
        List<WeeklyGoal> older = goals.stream().skip(3).limit(3).toList();
        if (older.isEmpty()) return "STABLE";
        
        double avgOlderProgress = older.stream()
                .mapToDouble(g -> g.getProgressPercentage().doubleValue())
                .average().orElse(0.0);

        if (avgRecentProgress > avgOlderProgress + 10) return "IMPROVING";
        if (avgRecentProgress < avgOlderProgress - 10) return "DECLINING";
        return "STABLE";
    }

    private Integer calculateCurrentStreak(List<WeeklyGoal> goals) {
        int streak = 0;
        for (WeeklyGoal goal : goals) {
            if (goal.getIsAchieved()) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private Integer calculateBestStreak(List<WeeklyGoal> goals) {
        int maxStreak = 0;
        int currentStreak = 0;
        
        for (WeeklyGoal goal : goals) {
            if (goal.getIsAchieved()) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }
        return maxStreak;
    }

    private Integer calculateTotalRewardPoints(List<WeeklyGoal> goals) {
        return goals.stream()
                .filter(WeeklyGoal::getIsAchieved)
                .mapToInt(WeeklyGoal::getRewardPoints)
                .sum();
    }

    private BigDecimal calculateAverageProgress(List<WeeklyGoal> goals) {
        if (goals.isEmpty()) return BigDecimal.ZERO;
        
        double avg = goals.stream()
                .mapToDouble(g -> g.getProgressPercentage().doubleValue())
                .average().orElse(0.0);
                
        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }

    private String generateMotivationalMessage(BigDecimal achievementRate, String trend) {
        if (achievementRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "🌟 Xuất sắc! Bạn đang tiến bộ rất tốt. Hãy tiếp tục!";
        } else if (achievementRate.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "👍 Bạn đang làm rất tốt! Chỉ cần cố gắng thêm một chút nữa.";
        } else if (trend.equals("IMPROVING")) {
            return "📈 Tuyệt vời! Bạn đang cải thiện. Đừng bỏ cuộc!";
        } else {
            return "💪 Đừng lo lắng, mọi người đều có lúc khó khăn. Hãy bắt đầu lại!";
        }
    }

    private BigDecimal calculateSmokingReductionRate(List<WeeklyGoal> goals) {
        if (goals.isEmpty()) return BigDecimal.ZERO;
        
        List<WeeklyGoal> achieved = goals.stream()
                .filter(WeeklyGoal::getIsAchieved)
                .toList();
                
        if (achieved.isEmpty()) return BigDecimal.ZERO;
        
        double avgReduction = achieved.stream()
                .mapToDouble(g -> {
                    if (g.getTargetSmokedCount() > 0) {
                        return (double)(g.getTargetSmokedCount() - g.getActualSmokedCount()) / g.getTargetSmokedCount() * 100;
                    }
                    return 0;
                })
                .average().orElse(0.0);
                
        return BigDecimal.valueOf(Math.max(0, avgReduction)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCravingResistanceRate(List<WeeklyGoal> goals) {
        if (goals.isEmpty()) return BigDecimal.ZERO;
        
        List<WeeklyGoal> achieved = goals.stream()
                .filter(WeeklyGoal::getIsAchieved)
                .toList();
                
        if (achieved.isEmpty()) return BigDecimal.ZERO;
        
        double avgResistance = achieved.stream()
                .mapToDouble(g -> {
                    if (g.getTargetCravingResistance() > 0) {
                        return Math.min(100.0, (double)g.getActualCravingResistance() / g.getTargetCravingResistance() * 100);
                    }
                    return 0;
                })
                .average().orElse(0.0);
                
        return BigDecimal.valueOf(avgResistance).setScale(2, RoundingMode.HALF_UP);
    }
}
