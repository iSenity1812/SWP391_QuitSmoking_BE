package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CravingTrackingRepository extends JpaRepository<CravingTracking, Integer> {
//    Optional<CravingTracking> findByDailySummaryAndTrackTime(DailySummary dailySummary, LocalDateTime trackTime);

    List<CravingTracking> findAllByDailySummary_QuitPlan_Member_MemberIdAndTrackTimeBetween(
            UUID memberId, LocalDateTime startTime, LocalDateTime endTime);

    List<CravingTracking> findByDailySummary_DailySummaryId(Integer dailySummaryId);
    List<CravingTracking> findByDailySummary(DailySummary dailySummary);
}
