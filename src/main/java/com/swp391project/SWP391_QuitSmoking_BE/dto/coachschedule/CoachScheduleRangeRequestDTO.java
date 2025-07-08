package com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CoachScheduleRangeRequestDTO {
    @NotNull(message = "Ngày bắt đầu không thể null")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hôm nay hoặc trong tương lai")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không thể null")
    private LocalDate endDate;

    @NotNull(message = "Danh sách ID khung giờ không thể null")
    private List<Integer> timeSlotIds;
}
