package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, UUID> {
    List<MemberSubscription> findByMember_MemberIdOrderByPurchasedAtDesc(UUID memberId);
    Optional<MemberSubscription> findFirstByMember_MemberIdAndSubscriptionStatusOrderByEndDateDesc(UUID memberId, com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus status);
}