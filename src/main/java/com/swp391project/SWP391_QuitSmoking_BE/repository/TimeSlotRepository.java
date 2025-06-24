package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    // Tìm kiếm các timeslot trong một khoảng thời gian cụ thể
    List<TimeSlot> findByStartTimeBetweenOrderByStartTimeAsc(LocalTime start, LocalTime end);

    // Tìm kiếm TimeSlot theo startTime
    Optional<TimeSlot> findByStartTime(LocalTime startTime);

    // Tìm tất cả các TimeSlot không bị xóa
    List<TimeSlot> findByIsDeletedFalse();

    List<TimeSlot> findByIsDeletedFalseOrderByStartTimeAsc();

    List<TimeSlot> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(LocalTime startTime, LocalTime endTime);

    Optional<TimeSlot> findByEndTime(LocalTime endTime);
}
