package com.swp391project.SWP391_QuitSmoking_BE.dto.health;

import com.swp391project.SWP391_QuitSmoking_BE.enums.HealthMetricType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetricDTO {
    
    private UUID id;
    private UUID userId;
    private HealthMetricType metricType;
    private String displayName;
    private String description;
    private Double currentProgress;
    private LocalDateTime targetDate;
    private LocalDateTime achievedDate;
    private Boolean isCompleted;
    private Double timeRemainingHours;
    private String timeRemainingFormatted;
    private Boolean hasRegressed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 