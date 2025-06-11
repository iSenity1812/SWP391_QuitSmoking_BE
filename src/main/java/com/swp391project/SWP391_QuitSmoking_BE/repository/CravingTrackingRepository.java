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
    // Tìm kiếm theo giờ
    Optional<CravingTracking> findByHour(int hour);

    // Lấy tất cả các bản ghi theo ngày
    List<CravingTracking> findAllByDate(LocalDate date);

    // Tìm kiếm theo ngày và giờ
    Optional<CravingTracking> findByDateAndHour(LocalDate date, int hour);

    Optional<CravingTracking> findByDateTime(LocalDateTime dateTime);

    // Lấy tất cả các bản ghi trong một khoảng thời gian nhất định
    List<CravingTracking> findAllByHourBetween(LocalDate startDate, LocalDate endDate);

    // Lấy tất cả các bản ghi theo ngày và giờ lớn hơn hoặc bằng
    List<CravingTracking> findAllByDateAndHourGreaterThanEqual(LocalDate date, int hour);
}
