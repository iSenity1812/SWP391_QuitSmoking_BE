package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CravingTrackingRepository extends JpaRepository<CravingTracking, Integer> {
    Optional<CravingTracking> findByDailySummaryAndTrackTime(DailySummary dailySummary, LocalDateTime trackTime);
    List<CravingTracking> findAllByTrackTime(LocalDate date);

    Optional<CravingTracking> findByTrackTime(LocalDateTime trackTime);
    List<CravingTracking> findByDailySummary_DailySummaryId(Integer dailySummaryId);
    List<CravingTracking> findByDailySummary(DailySummary dailySummary);

    // Lấy tất cả các bản ghi trong một khoảng thời gian nhất định
    List<CravingTracking> findAllByTrackTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
