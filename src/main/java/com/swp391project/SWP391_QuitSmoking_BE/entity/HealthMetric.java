package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.HealthMetricType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "health_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false)
    private HealthMetricType metricType;
    
    @Column(name = "current_progress", nullable = false)
    private Double currentProgress; // 0.0 to 100.0
    
    @Column(name = "target_date")
    private LocalDateTime targetDate;
    
    @Column(name = "achieved_date")
    private LocalDateTime achievedDate;
    
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "time_remaining_hours")
    private Long timeRemainingHours;
    
    @Column(name = "has_regressed", nullable = false)
    private Boolean hasRegressed = false; // Đánh dấu đã từng tụt xuống dưới 100% sau khi hoàn thành
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 