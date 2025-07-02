package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserQuitStatsResponse {
    private long daysWithoutSmoking;
    private long cigarettesAvoided;
    private long moneySaved;
} 