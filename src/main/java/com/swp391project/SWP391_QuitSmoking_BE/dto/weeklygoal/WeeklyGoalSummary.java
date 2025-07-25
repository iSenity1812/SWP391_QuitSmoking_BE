package com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyGoalSummary {
    
    // Overall statistics
    private Integer totalGoals;
    private Integer achievedGoals;
    private Integer failedGoals;
    private Integer inProgressGoals;
    
    // Success rates
    private BigDecimal achievementRate;
    private Integer currentStreak;
    private Integer bestStreak;
    
    // Rewards
    private Integer totalRewardPoints;
    private Integer weeklyRewardPoints;
    
    // Current week info
    private WeeklyGoalResponse currentWeekGoal;
    private Boolean hasCurrentWeekGoal;
    
    // Recent goals
    private List<WeeklyGoalResponse> recentGoals;
    
    // Progress trends
    private BigDecimal averageProgress;
    private String progressTrend; // "IMPROVING", "STABLE", "DECLINING"
    
    // Recommendations
    private String recommendedDifficulty;
    private Integer suggestedSmokedTarget;
    private Integer suggestedCravingTarget;
    private String motivationalMessage;
    
    // Performance metrics
    private BigDecimal smokingReductionRate;
    private BigDecimal cravingResistanceRate;
    
    public static WeeklyGoalSummaryBuilder builder() {
        return new WeeklyGoalSummaryBuilder();
    }
    
    public static class WeeklyGoalSummaryBuilder {
        private Integer totalGoals = 0;
        private Integer achievedGoals = 0;
        private Integer failedGoals = 0;
        private Integer inProgressGoals = 0;
        private BigDecimal achievementRate = BigDecimal.ZERO;
        private Integer currentStreak = 0;
        private Integer bestStreak = 0;
        private Integer totalRewardPoints = 0;
        private Integer weeklyRewardPoints = 0;
        private WeeklyGoalResponse currentWeekGoal;
        private Boolean hasCurrentWeekGoal = false;
        private List<WeeklyGoalResponse> recentGoals;
        private BigDecimal averageProgress = BigDecimal.ZERO;
        private String progressTrend = "STABLE";
        private String recommendedDifficulty = "NORMAL";
        private Integer suggestedSmokedTarget = 0;
        private Integer suggestedCravingTarget = 5;
        private String motivationalMessage = "Hãy bắt đầu hành trình cai thuốc của bạn!";
        private BigDecimal smokingReductionRate = BigDecimal.ZERO;
        private BigDecimal cravingResistanceRate = BigDecimal.ZERO;
        
        public WeeklyGoalSummaryBuilder totalGoals(Integer totalGoals) {
            this.totalGoals = totalGoals;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder achievedGoals(Integer achievedGoals) {
            this.achievedGoals = achievedGoals;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder failedGoals(Integer failedGoals) {
            this.failedGoals = failedGoals;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder inProgressGoals(Integer inProgressGoals) {
            this.inProgressGoals = inProgressGoals;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder achievementRate(BigDecimal achievementRate) {
            this.achievementRate = achievementRate;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder currentStreak(Integer currentStreak) {
            this.currentStreak = currentStreak;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder bestStreak(Integer bestStreak) {
            this.bestStreak = bestStreak;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder totalRewardPoints(Integer totalRewardPoints) {
            this.totalRewardPoints = totalRewardPoints;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder weeklyRewardPoints(Integer weeklyRewardPoints) {
            this.weeklyRewardPoints = weeklyRewardPoints;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder currentWeekGoal(WeeklyGoalResponse currentWeekGoal) {
            this.currentWeekGoal = currentWeekGoal;
            this.hasCurrentWeekGoal = currentWeekGoal != null;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder recentGoals(List<WeeklyGoalResponse> recentGoals) {
            this.recentGoals = recentGoals;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder averageProgress(BigDecimal averageProgress) {
            this.averageProgress = averageProgress;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder progressTrend(String progressTrend) {
            this.progressTrend = progressTrend;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder recommendedDifficulty(String recommendedDifficulty) {
            this.recommendedDifficulty = recommendedDifficulty;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder suggestedSmokedTarget(Integer suggestedSmokedTarget) {
            this.suggestedSmokedTarget = suggestedSmokedTarget;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder suggestedCravingTarget(Integer suggestedCravingTarget) {
            this.suggestedCravingTarget = suggestedCravingTarget;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder motivationalMessage(String motivationalMessage) {
            this.motivationalMessage = motivationalMessage;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder smokingReductionRate(BigDecimal smokingReductionRate) {
            this.smokingReductionRate = smokingReductionRate;
            return this;
        }
        
        public WeeklyGoalSummaryBuilder cravingResistanceRate(BigDecimal cravingResistanceRate) {
            this.cravingResistanceRate = cravingResistanceRate;
            return this;
        }
        
        public WeeklyGoalSummary build() {
            return new WeeklyGoalSummary(
                totalGoals, achievedGoals, failedGoals, inProgressGoals,
                achievementRate, currentStreak, bestStreak,
                totalRewardPoints, weeklyRewardPoints,
                currentWeekGoal, hasCurrentWeekGoal, recentGoals,
                averageProgress, progressTrend,
                recommendedDifficulty, suggestedSmokedTarget, suggestedCravingTarget,
                motivationalMessage, smokingReductionRate, cravingResistanceRate
            );
        }
    }
}
