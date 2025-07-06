package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MemberAchievementRepository extends JpaRepository<MemberAchievement, Long> {
    List<MemberAchievement> findByMember(Member member);
    List<MemberAchievement> findByMember_MemberId(UUID memberId);
    boolean existsByMember_MemberIdAndAchievementId(UUID memberId, Long achievementId);
} 