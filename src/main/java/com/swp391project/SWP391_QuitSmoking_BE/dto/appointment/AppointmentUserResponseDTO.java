package com.swp391project.SWP391_QuitSmoking_BE.dto.appointment;

import com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot.TimeSlotResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUserResponseDTO {
    private Long appointmentId;
//    private UserSimpleResponseDTO coach; // Thông tin Coach đơn giản
    private String fullName; // Tên đăng nhập của Coach
    private String email; // Email của Coach
    private LocalDate scheduleDate; // Ngày hẹn
    private TimeSlotResponseDTO timeSlot; // Thông tin TimeSlot
    private AppointmentStatus status;
    private String note;
    private LocalDateTime bookingTime; // Thời gian đặt lịch
}
