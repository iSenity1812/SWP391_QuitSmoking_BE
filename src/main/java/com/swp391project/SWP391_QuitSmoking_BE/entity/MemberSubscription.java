//package com.swp391project.SWP391_QuitSmoking_BE.entity;
//
//import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus;
//import com.swp391project.SWP391_QuitSmoking_BE.validation.subscription.ValidSubscriptionDuration;
//import com.swp391project.SWP391_QuitSmoking_BE.validation.subscription.ValidSubscriptionState;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@ValidSubscriptionDuration  //Custom annotation cho thời lượng đăng kí
//@ValidSubscriptionState     //Custom annotation kiểm tra tính nhất quán về trạng thái đăng kí
//@Table(name = "MemberSubscriptions")
//public class MemberSubscription {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "MemberSubcriptionID", nullable = false, updatable = false, columnDefinition = "uuid")
//    private UUID memberSubscriptionId;
//
//    @NotNull(message = "Member không được để trống")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "MemberID", referencedColumnName = "MemberID", nullable = false)
//    private Member member;
//
//    @NotNull(message = "Subscription không được để trống")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "SubscriptionID", referencedColumnName = "SubscriptionID", nullable = false)
//    private Subscription subscription;
//
//    @NotNull(message = "StartDate không được để trống")
//    @Column(name = "StartDate", nullable = false)
//    private LocalDateTime startDate;
//
//    @NotNull(message = "EndDate không được để trống")
//    @Column(name = "EndDate", nullable = false)
//    private LocalDateTime endDate;
//
//    @NotNull(message = "Trạng thái đăng ký không được để trống")
//    @Enumerated(EnumType.STRING)
//    @Column(name = "SubscriptionStatus", nullable = false)
//    private SubscriptionStatus subscriptionStatus;
//
//    @Column(name = "PurchasedAt", nullable = false, updatable = false)
//    private LocalDateTime purchasedAt = LocalDateTime.now();
//}
