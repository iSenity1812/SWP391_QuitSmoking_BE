package com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Mood;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailySummaryUpdateRequest {
//    @NotNull(message = "ID nhật ký hàng ngày không được để trống")
//    private Integer dailySummaryId;

//    @PastOrPresent(message = "Ngày theo dõi không thể ở tương lai")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    private LocalDate trackDate;

    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    private Integer updateSmokedCount;
    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    private Integer updateCravingCount;
    private Mood mood;
    private String note;
}
