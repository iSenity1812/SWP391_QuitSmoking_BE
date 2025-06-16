package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "member_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSubscription {
    @Id
    @Column(name = "member_subcriptionid")
    private UUID memberSubcriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriptionid", nullable = false)
    private Subscription subscription;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;
}