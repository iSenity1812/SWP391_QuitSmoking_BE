package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, Integer> {
    //được sử dụng trong DailySummaryDatesValidator để kiểm tra tính duy nhất
    //có thể chứa một giá trị non-null hoặc null
    Optional<DailySummary> findByQuitPlanAndTrackDate(QuitPlan quitPlan, LocalDate trackDate);
    List<DailySummary> findByQuitPlanAndTrackDateBetween(QuitPlan quitPlan, LocalDate startDate, LocalDate endDate);
}
