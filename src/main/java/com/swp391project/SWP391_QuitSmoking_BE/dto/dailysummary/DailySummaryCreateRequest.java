package com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Mood;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySummaryCreateRequest {
//    @NotNull(message = "Member ID không được để trống")
//    private UUID memberId; // Member ID để liên kết với QuitPlan

//    @NotNull(message = "Ngày theo dõi không được để trống")
    @PastOrPresent(message = "Ngày theo dõi không thể ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate trackDate;

    @Min(value = 0, message = "Tổng số điếu đã hút không thể là số âm")
    @Max(value = 1000, message = "Số lượng thuốc đã hút không thể vượt quá 1000 điếu")
    private Integer totalSmokedCount; // Số điếu đã hút (mặc định 0)
    @Min(value = 0, message = "Tổng số lần thèm thuốc không thể là số âm")
    @Max(value = 1000, message = "Số lần thèm thuốc không thể vượt quá 1000 lần")
    private Integer totalCravingCount; // Số lần thèm thuốc (mặc định 0)

    private Mood mood;
    private String note;
}
