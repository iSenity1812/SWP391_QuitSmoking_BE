package com.swp391project.SWP391_QuitSmoking_BE.dto.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthOverviewDTO {
    private Long totalMetrics;
    private Long completedMetrics;
    private Long inProgressMetrics;
    private Long regressedMetrics;
    private Double overallProgress;
    private List<HealthMetricDTO> topProgressMetrics;
    private List<HealthMetricDTO> upcomingMilestones;
    private List<HealthMetricDTO> recentAchievements;
    private String nextMilestone;
    private Long daysSinceQuit;
    private Long hoursSinceQuit;
    private List<HealthMetricDTO> metrics;
} 