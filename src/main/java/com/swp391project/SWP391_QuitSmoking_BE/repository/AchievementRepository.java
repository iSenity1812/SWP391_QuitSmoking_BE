package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    boolean existsByName(String name);
    
    @Query("SELECT a FROM Achievement a WHERE a.achievementId IN (SELECT ma.achievementId FROM MemberAchievement ma WHERE ma.member.memberId = :memberId)")
    List<Achievement> findUnlockedAchievementsByMember_MemberId(@Param("memberId") UUID memberId);
    
    @Query("SELECT a FROM Achievement a WHERE a.achievementId NOT IN (SELECT ma.achievementId FROM MemberAchievement ma WHERE ma.member.memberId = :memberId)")
    List<Achievement> findLockedAchievementsByMember_MemberId(@Param("memberId") UUID memberId);
} 