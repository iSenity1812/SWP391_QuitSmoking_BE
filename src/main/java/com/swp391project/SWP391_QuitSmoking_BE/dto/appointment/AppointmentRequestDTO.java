package com.swp391project.SWP391_QuitSmoking_BE.dto.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    @NotNull(message = "Coach Schedule ID cannot be null")
    private Long coachScheduleId; // ID của CoachSchedule mà thành viên muốn đặt

    private String email; // Email của thành viên muốn đặt lịch hẹn

    private String note; // Ghi chú của thành viên

//    private UUID memberIdToBook; // ID của thành viên muốn đặt lịch hẹn (id member mà coach đặt hộ)
}
