package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.DurationType;
import com.swp391project.SWP391_QuitSmoking_BE.interfaces.IDurationAware;
import com.swp391project.SWP391_QuitSmoking_BE.validation.ValidDurationTypeConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidDurationTypeConstraint //custom annotation kiểm tra giá trị giữa durationType và duration
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    @Column(name = "SubscriptionID", updatable = false, nullable = false)
    private Long subscriptionId;

    // Mối quan hệ Many-to-One với User
    // Subscription thuộc về một User
    @NotNull(message = "Người dùng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false, foreignKey = @ForeignKey(name = "FK_Subscription_User"))
    private User user;

    // Mối quan hệ Many-to-One với Plan
    // Subscription liên quan đến một Plan
    @NotNull(message = "Gói không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanID", nullable = false, foreignKey = @ForeignKey(name = "FK_Subscription_Plan"))
    private Plan plan;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc không thể ở quá khứ")
    @Column(name = "EndDate", nullable = false)
    private LocalDateTime endDate;

    @NotNull(message = "Trạng thái hoạt động không được để trống")
    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true;


    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Transaction transaction;


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
