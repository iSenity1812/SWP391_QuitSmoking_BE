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
    // Them vao specialties
    private String specialties; // Chuỗi chứa các chuyên môn của Coach, ví dụ: "Chuyên môn 1, Chuyên môn 2"
    private double rating;
}
