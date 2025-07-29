package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface MemberAchievementRepository extends JpaRepository<MemberAchievement, Long> {
    List<MemberAchievement> findByMember(Member member);
    List<MemberAchievement> findByMember_MemberId(UUID memberId);
    boolean existsByMember_MemberIdAndAchievementId(UUID memberId, Long achievementId);
    java.util.Optional<MemberAchievement> findByMember_MemberIdAndAchievementId(UUID memberId, Long achievementId);
    long countByMember_MemberId(UUID memberId);

    List<MemberAchievement> findTop5ByMemberOrderByDateAchievedDesc(Member member);

    long countByMember(Member member);
    List<MemberAchievement> findByMemberAndIsShared(Member member, boolean isShared);
    List<MemberAchievement> findByMemberAndIsSharedTrueOrderByDateAchievedDesc(Member member);
    List<MemberAchievement> findTop3ByMemberOrderByDateAchievedDesc(Member member);
    
    @Modifying
    @Transactional
    @Query("UPDATE MemberAchievement ma SET ma.isShared = :isShared WHERE ma.memberId = :memberId AND ma.achievementId = :achievementId")
    int updateShareStatus(@Param("memberId") UUID memberId, @Param("achievementId") Long achievementId, @Param("isShared") boolean isShared);
}