package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CravingTrackingRepository extends JpaRepository<CravingTracking, Integer> {
    //tìm record theo DailySummary và TrackTime
    //được sử dụng trong CravingTrackingDataValidator để kiểm tra tính duy nhất
    Optional<CravingTracking> findByDailySummaryAndTrackTime(DailySummary dailySummary, LocalDateTime trackTime);
}
