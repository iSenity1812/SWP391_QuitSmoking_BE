package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAdminResponseDTO {
    private Long subscriptionId;
    private String packageName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal price;
    private String status;
}
