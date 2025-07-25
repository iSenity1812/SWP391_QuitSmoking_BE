package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "weekly_goal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_goal_id")
    private Long weeklyGoalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quit_plan_id", nullable = false)
    @NotNull(message = "Quit plan không được để trống")
    private QuitPlan quitPlan;

    @Column(name = "week_start_date", nullable = false)
    @NotNull(message = "Ngày bắt đầu tuần không được để trống")
    private LocalDate weekStartDate;

    @Column(name = "week_end_date", nullable = false)
    @NotNull(message = "Ngày kết thúc tuần không được để trống")
    private LocalDate weekEndDate;

    @Column(name = "target_smoked_count", nullable = false)
    @Min(value = 0, message = "Mục tiêu số thuốc lá không thể âm")
    @Max(value = 500, message = "Mục tiêu số thuốc lá không thể vượt quá 500")
    private Integer targetSmokedCount;

    @Column(name = "target_craving_resistance", nullable = false)
    @Min(value = 0, message = "Mục tiêu chống chọi cơn thèm không thể âm")
    private Integer targetCravingResistance;

    @Column(name = "actual_smoked_count")
    @Min(value = 0, message = "Số thuốc lá thực tế không thể âm")
    private Integer actualSmokedCount = 0;

    @Column(name = "actual_craving_resistance")
    @Min(value = 0, message = "Số lần chống chọi thực tế không thể âm")
    private Integer actualCravingResistance = 0;

    @Column(name = "progress_percentage", precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "Tỷ lệ hoàn thành không thể âm")
    @DecimalMax(value = "100.00", message = "Tỷ lệ hoàn thành không thể vượt quá 100%")
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @Column(name = "is_achieved", nullable = false)
    private Boolean isAchieved = false;

    @Column(name = "reward_points")
    @Min(value = 0, message = "Điểm thưởng không thể âm")
    private Integer rewardPoints = 0;

    @Column(name = "difficulty_level", length = 20)
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel = DifficultyLevel.NORMAL;

    @Column(name = "goal_type", length = 30)
    @Enumerated(EnumType.STRING)
    private GoalType goalType = GoalType.REDUCTION;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Enum cho difficulty level
    public enum DifficultyLevel {
        EASY,       // Mục tiêu dễ (target cao hơn baseline)
        NORMAL,     // Mục tiêu bình thường
        HARD,       // Mục tiêu khó (target thấp hơn baseline)
        EXTREME     // Mục tiêu cực khó
    }

    // Enum cho goal type
    public enum GoalType {
        REDUCTION,      // Giảm số lượng thuốc
        RESISTANCE,     // Tăng khả năng chống chọi
        COMBINED,       // Kết hợp cả hai
        MAINTENANCE     // Duy trì trạng thái hiện tại
    }

    // Helper methods
    public void updateProgress() {
        if (targetSmokedCount == null || targetSmokedCount == 0) {
            this.progressPercentage = BigDecimal.ZERO;
            return;
        }

        // Calculate progress based on both smoking reduction and craving resistance
        BigDecimal smokingProgress = BigDecimal.ZERO;
        BigDecimal cravingProgress = BigDecimal.ZERO;

        // Smoking progress (inverted - less smoking is better)
        if (actualSmokedCount != null) {
            BigDecimal smokingRatio = BigDecimal.valueOf(targetSmokedCount - actualSmokedCount)
                    .divide(BigDecimal.valueOf(targetSmokedCount), 4, java.math.RoundingMode.HALF_UP);
            smokingProgress = smokingRatio.multiply(BigDecimal.valueOf(100));
            if (smokingProgress.compareTo(BigDecimal.ZERO) < 0) {
                smokingProgress = BigDecimal.ZERO;
            }
        }

        // Craving resistance progress
        if (targetCravingResistance != null && targetCravingResistance > 0 && actualCravingResistance != null) {
            BigDecimal cravingRatio = BigDecimal.valueOf(actualCravingResistance)
                    .divide(BigDecimal.valueOf(targetCravingResistance), 4, java.math.RoundingMode.HALF_UP);
            cravingProgress = cravingRatio.multiply(BigDecimal.valueOf(100));
            if (cravingProgress.compareTo(BigDecimal.valueOf(100)) > 0) {
                cravingProgress = BigDecimal.valueOf(100);
            }
        }

        // Weighted average (70% smoking, 30% craving)
        this.progressPercentage = smokingProgress.multiply(BigDecimal.valueOf(0.7))
                .add(cravingProgress.multiply(BigDecimal.valueOf(0.3)));

        // Check achievement
        this.isAchieved = this.progressPercentage.compareTo(BigDecimal.valueOf(80)) >= 0;
        
        if (this.isAchieved && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public boolean isCurrentWeek() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(weekStartDate) && !now.isAfter(weekEndDate);
    }

    public int getDaysRemaining() {
        LocalDate now = LocalDate.now();
        if (now.isAfter(weekEndDate)) {
            return 0;
        }
        return (int) now.until(weekEndDate).getDays() + 1;
    }
}
