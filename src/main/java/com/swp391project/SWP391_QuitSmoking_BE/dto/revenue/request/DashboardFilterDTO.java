package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFilterDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TransactionStatus> statuses;
    private List<Integer> planIds;
    private List<UUID> userIds;
    private String paymentMethod;
    private String period; // "TODAY", "YESTERDAY", "LAST_7_DAYS", "LAST_30_DAYS", "THIS_MONTH", "LAST_MONTH", "THIS_YEAR", "CUSTOM"

    // Pagination
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;
    private String sortBy = "transactionDate";
    private String sortDirection = "DESC";
}
