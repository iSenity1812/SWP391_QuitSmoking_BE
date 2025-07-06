package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RevenueTimeGroupDTO {
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private String groupBy; // "DAY", "WEEK", "MONTH", "YEAR"
}