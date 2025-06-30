//package com.swp391project.SWP391_QuitSmoking_BE.repository;
//
//import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberSubscription;
//import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, UUID> {
//    List<MemberSubscription> findByMemberOrderByPurchasedAtDesc(Member member);
//
//    // Tìm các gói đăng ký của một thành viên theo trạng thái
//    List<MemberSubscription> findByMemberAndSubscriptionStatus(Member member, SubscriptionStatus status);
//}
