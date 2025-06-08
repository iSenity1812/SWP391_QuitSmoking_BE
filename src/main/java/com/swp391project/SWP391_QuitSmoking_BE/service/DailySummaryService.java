package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

class DailySummaryEditForbiddenException extends RuntimeException {
    public DailySummaryEditForbiddenException(String message) {
        super(message);
    }
}

@AllArgsConstructor
@Service
public class DailySummaryService {
    private final DailySummaryRepository dailySummaryRepository;

    @Transactional
    public DailySummary createDailySummary(DailySummary dailySummary) {
        return dailySummaryRepository.save(dailySummary);
    }

    @Transactional
    public DailySummary updateDailySummary(Integer dailySummaryId, DailySummary updatedDailySummary) {
        Optional<DailySummary> existingSummaryOptional = dailySummaryRepository.findById(dailySummaryId);

        if (existingSummaryOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy nhật ký hàng ngày với ID: " + dailySummaryId);
        }

        DailySummary existingSummary = existingSummaryOptional.get();
        LocalDate today = LocalDate.now();

        //Nếu TrackDate đã vượt qua ngày hiện tại, không cho phép chỉnh sửa
        if (existingSummary.getTrackDate().isBefore(today)) {
            throw new DailySummaryEditForbiddenException("Không thể chỉnh sửa nhật ký hàng ngày cho các ngày đã qua");
        }
        // Nếu TrackDate là ngày hiện tại, cho phép chỉnh sửa
        // Không cho cập nhật TrackDate, QuitPlanID
        existingSummary.setTotalSmokedCount(updatedDailySummary.getTotalSmokedCount());
        existingSummary.setTotalCravingCount(updatedDailySummary.getTotalCravingCount());
        existingSummary.setMood(updatedDailySummary.getMood());
        existingSummary.setNote(updatedDailySummary.getNote());
        existingSummary.setMoneySaved(updatedDailySummary.getMoneySaved());
        existingSummary.setGoalAchievedToday(updatedDailySummary.isGoalAchievedToday());

        return dailySummaryRepository.save(existingSummary);
    }

    // Các phương thức service khác...
}
