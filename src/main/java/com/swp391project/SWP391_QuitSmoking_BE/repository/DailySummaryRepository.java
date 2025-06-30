package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, Integer> {
    //được sử dụng trong DailySummaryDatesValidator để kiểm tra tính duy nhất
    //có thể chứa một giá trị non-null hoặc null
    Optional<DailySummary> findByQuitPlanAndTrackDate(QuitPlan quitPlan, LocalDate trackDate);
    List<DailySummary> findByQuitPlan_Member_MemberIdAndTrackDateBetween(UUID memberId, LocalDate startDate, LocalDate endDate);
    List<DailySummary> findByQuitPlan(QuitPlan quitPlan);

    List<DailySummary> findByQuitPlanAndTrackDateBetween
            (QuitPlan plan,
             LocalDate localDate,
             @NotNull(message = "Ngày kết thúc mục tiêu không được để trống")
             @FutureOrPresent(message = "Ngày kết thúc mục tiêu phải ở hiện tại hoặc tương lai")
             LocalDate goalDate);

    List<DailySummary> findByQuitPlanOrderByTrackDateDesc(QuitPlan quitPlan);
    Optional<DailySummary> findByTrackDate(LocalDate trackDate); //tìm kiếm theo ngày track

    // Method for achievement calculation
    List<DailySummary> findByQuitPlan_QuitPlanId(Integer quitPlanId);
}
