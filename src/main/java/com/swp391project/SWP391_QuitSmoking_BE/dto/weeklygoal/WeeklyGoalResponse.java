package com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal;

import com.swp391project.SWP391_QuitSmoking_BE.entity.WeeklyGoal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyGoalResponse {
    
    private Long weeklyGoalId;
    private Integer quitPlanId;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    
    // Target values
    private Integer targetSmokedCount;
    private Integer targetCravingResistance;
    
    // Actual values
    private Integer actualSmokedCount;
    private Integer actualCravingResistance;
    
    // Progress
    private BigDecimal progressPercentage;
    private Boolean isAchieved;
    private Integer rewardPoints;
    
    // Metadata
    private WeeklyGoal.DifficultyLevel difficultyLevel;
    private WeeklyGoal.GoalType goalType;
    private String notes;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    
    // Computed fields
    private Boolean isCurrentWeek;
    private Integer daysRemaining;
    private String status; // "IN_PROGRESS", "COMPLETED", "FAILED", "UPCOMING"
    
    // Progress details
    private BigDecimal smokingProgress;
    private BigDecimal cravingProgress;
    private String progressDescription;
    
    public static WeeklyGoalResponse fromEntity(WeeklyGoal weeklyGoal) {
        if (weeklyGoal == null) {
            return null;
        }
        
        return WeeklyGoalResponse.builder()
                .weeklyGoalId(weeklyGoal.getWeeklyGoalId())
                .quitPlanId(weeklyGoal.getQuitPlan().getQuitPlanId())
                .weekStartDate(weeklyGoal.getWeekStartDate())
                .weekEndDate(weeklyGoal.getWeekEndDate())
                .targetSmokedCount(weeklyGoal.getTargetSmokedCount())
                .targetCravingResistance(weeklyGoal.getTargetCravingResistance())
                .actualSmokedCount(weeklyGoal.getActualSmokedCount())
                .actualCravingResistance(weeklyGoal.getActualCravingResistance())
                .progressPercentage(weeklyGoal.getProgressPercentage())
                .isAchieved(weeklyGoal.getIsAchieved())
                .rewardPoints(weeklyGoal.getRewardPoints())
                .difficultyLevel(weeklyGoal.getDifficultyLevel())
                .goalType(weeklyGoal.getGoalType())
                .notes(weeklyGoal.getNotes())
                .createdAt(weeklyGoal.getCreatedAt())
                .updatedAt(weeklyGoal.getUpdatedAt())
                .completedAt(weeklyGoal.getCompletedAt())
                .isCurrentWeek(weeklyGoal.isCurrentWeek())
                .daysRemaining(weeklyGoal.getDaysRemaining())
                .status(determineStatus(weeklyGoal))
                .smokingProgress(calculateSmokingProgress(weeklyGoal))
                .cravingProgress(calculateCravingProgress(weeklyGoal))
                .progressDescription(generateProgressDescription(weeklyGoal))
                .build();
    }
    
    private static String determineStatus(WeeklyGoal goal) {
        LocalDate now = LocalDate.now();
        
        if (goal.getIsAchieved()) {
            return "COMPLETED";
        }
        
        if (now.isBefore(goal.getWeekStartDate())) {
            return "UPCOMING";
        }
        
        if (now.isAfter(goal.getWeekEndDate())) {
            return "FAILED";
        }
        
        return "IN_PROGRESS";
    }
    
    private static BigDecimal calculateSmokingProgress(WeeklyGoal goal) {
        if (goal.getTargetSmokedCount() == null || goal.getTargetSmokedCount() == 0) {
            return BigDecimal.ZERO;
        }
        
        Integer actual = goal.getActualSmokedCount() != null ? goal.getActualSmokedCount() : 0;
        BigDecimal progress = BigDecimal.valueOf(goal.getTargetSmokedCount() - actual)
                .divide(BigDecimal.valueOf(goal.getTargetSmokedCount()), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        return progress.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
    }
    
    private static BigDecimal calculateCravingProgress(WeeklyGoal goal) {
        if (goal.getTargetCravingResistance() == null || goal.getTargetCravingResistance() == 0) {
            return BigDecimal.ZERO;
        }
        
        Integer actual = goal.getActualCravingResistance() != null ? goal.getActualCravingResistance() : 0;
        BigDecimal progress = BigDecimal.valueOf(actual)
                .divide(BigDecimal.valueOf(goal.getTargetCravingResistance()), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        return progress.min(BigDecimal.valueOf(100));
    }
    
    private static String generateProgressDescription(WeeklyGoal goal) {
        BigDecimal progress = goal.getProgressPercentage();
        
        if (progress.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "Xuất sắc! Bạn đang hoàn thành rất tốt mục tiêu tuần này.";
        } else if (progress.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "Tốt lắm! Bạn đang tiến gần đến mục tiêu.";
        } else if (progress.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "Bạn đang ở giữa chặng đường. Hãy cố gắng thêm!";
        } else if (progress.compareTo(BigDecimal.valueOf(25)) >= 0) {
            return "Vẫn còn thời gian để cải thiện. Đừng bỏ cuộc!";
        } else {
            return "Hãy tập trung vào mục tiêu. Bạn có thể làm được!";
        }
    }
}
