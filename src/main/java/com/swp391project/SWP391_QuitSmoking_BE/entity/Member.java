package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.validation.subscription.ValidSubscriptionDuration;
import com.swp391project.SWP391_QuitSmoking_BE.validation.subscription.ValidSubscriptionState;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidSubscriptionDuration
@ValidSubscriptionState
public class Member {
    @Id
    @Column(name = "MemberID")
    private UUID memberId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "MemberID", referencedColumnName = "UserID")
    @NotNull(message = "Người dùng liên kết không được để trống")
    private User user;

    // Các trường liên quan đến gói đăng ký trực tiếp trên Member, theo DB schema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubscriptionID", referencedColumnName = "SubscriptionID")
    private Subscription subscription; // Getter: getSubscription(), Setter: setSubscription()

    @Column(name = "StartDate")
    private LocalDateTime startDate; // Getter: getStartDate(), Setter: setStartDate()

    @Column(name = "EndDate")
    private LocalDateTime endDate; // Getter: getEndDate(), Setter: setEndDate()

    @Column(name = "SubscriptionStatus", nullable = false)
    private boolean subscriptionStatus = false; // Thay đổi từ enum sang boolean. Getter sẽ là isSubscriptionStatus()

    @Min(value = 0, message = "Streak không thể là số âm")
    @Column(name = "Streak", nullable = false)
    private int streak = 0;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSubscription> memberSubscriptions;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuitPlan> quitPlans;
}