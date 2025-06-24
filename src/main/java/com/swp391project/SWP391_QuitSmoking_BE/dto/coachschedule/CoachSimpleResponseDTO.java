package com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachSimpleResponseDTO {
    private UUID coachId;
    private String username;
    private String email;
    private String fullName; // Thêm fullName từ Coach entity
    private double rating;
}
