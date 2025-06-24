package com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot;

import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredSlotDTO {
    private Integer coachScheduleId; // ID của lịch trình của Coach
    private LocalDate date;
    private Integer timeSlotId;
    private String label;
    private String startTime;
    private String endTime;
    private boolean isAvailable;
    private List<AppointmentDetailsDTO> appointmentDetails; // Thông tin chi tiết về cuộc hẹn nếu đã đăng ký, có thể rỗng nếu không có cuộc hẹn
}
