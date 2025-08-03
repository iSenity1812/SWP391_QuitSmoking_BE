package com.swp391project.SWP391_QuitSmoking_BE.dto.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetricDTO {
    private String id;
    private String metricType;
    private Double currentProgress;
    private Boolean isCompleted;
    private Boolean hasRegressed;
    private String description;
    private LocalDateTime targetDate;
    private LocalDateTime achievedDate;
    private Double timeRemainingHours;
    private String timeRemainingFormatted;
    private String displayName;
} 