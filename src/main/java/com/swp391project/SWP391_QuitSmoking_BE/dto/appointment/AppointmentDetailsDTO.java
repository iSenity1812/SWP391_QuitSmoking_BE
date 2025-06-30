package com.swp391project.SWP391_QuitSmoking_BE.dto.appointment;

import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailsDTO {
    private Long appointmentId;
    private String clientName; // Tên của Member đặt lịch
    private UUID clientId;       // ID của Member đặt lịch
    private AppointmentStatus status;
    private String notes;
}
