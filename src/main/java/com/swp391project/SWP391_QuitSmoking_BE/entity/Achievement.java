package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AchievementID")
    private Long achievementId;

    @Column(name = "achievement_name", nullable = false, unique = true)
    private String name;

    @Column(name = "IconUrl")
    private String iconUrl;

    @Column(name = "Description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "AchievementType", nullable = false)
    private AchievementType achievementType;

    @Column(name = "milestone_value", nullable = false)
    private BigDecimal milestoneValue;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    public enum AchievementType {
        DAYS_QUIT,           // Số ngày cai thuốc
        MONEY_SAVED,         // Số tiền tiết kiệm (VND)
        CIGARETTES_NOT_SMOKED, // Số điếu không hút
        DAILY,               // Thành tựu daily
        RESILIENCE,          // Thành tựu kiên trì/quay lại
        HEALTH,              // Thành tựu sức khỏe
        SOCIAL,              // Thành tựu cộng đồng
        SPECIAL              // Thành tựu đặc biệt
    }
} 