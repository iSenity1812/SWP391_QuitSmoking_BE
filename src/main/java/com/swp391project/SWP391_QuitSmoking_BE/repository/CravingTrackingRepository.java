package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CravingTrackingRepository extends JpaRepository<CravingTracking, Integer> {
//    Optional<CravingTracking> findByDailySummaryAndTrackTime(DailySummary dailySummary, LocalDateTime trackTime);

    List<CravingTracking> findAllByDailySummary_QuitPlan_Member_MemberIdAndTrackTimeBetween(
            UUID memberId, LocalDateTime startTime, LocalDateTime endTime);

    // Phương thức tìm kiếm bản ghi theo dõi cơn thèm trong một khoảng thời gian
    // và thuộc về kế hoạch cai thuốc của thành viên đó với trạng thái cụ thể.
    List<CravingTracking> findAllByDailySummary_QuitPlan_Member_MemberIdAndTrackTimeBetweenAndDailySummary_QuitPlan_StatusIn(
            UUID memberId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Collection<QuitPlanStatus> statuses
    );

    List<CravingTracking> findByDailySummary_DailySummaryId(Integer dailySummaryId);
    List<CravingTracking> findByDailySummary(DailySummary dailySummary);

    long countByDailySummary_QuitPlan_Member(Member member);
    long countByDailySummary_QuitPlan_MemberAndTrackTimeBetween(Member member, LocalDateTime startTime, LocalDateTime endTime);

    // Trong CravingTrackingRepository, thêm method mới:
    @Query("SELECT SUM(ct.cravingsCount) FROM CravingTracking ct " +
            "JOIN ct.dailySummary ds " +
            "WHERE ds.quitPlan.member = :member")
    Long sumCravingsCountByMember(@Param("member") Member member);
}
