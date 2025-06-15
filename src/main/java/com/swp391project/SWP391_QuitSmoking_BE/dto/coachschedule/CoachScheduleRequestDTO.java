package com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoachScheduleRequestDTO {
    // Coach ID sẽ được lấy từ token, ko cần gửi trong request body
    @NotNull(message = "TimeSlot ID không thể null")
    private Integer timeSlotId; // ID của TimeSlot

    @NotNull(message = "Schedule date không thể null")
    @FutureOrPresent(message = "Schedule date must be today or in the future") // Đảm bảo không tạo lịch trong quá khứ
    private LocalDate scheduleDate; // Ngày của lịch trình
}
