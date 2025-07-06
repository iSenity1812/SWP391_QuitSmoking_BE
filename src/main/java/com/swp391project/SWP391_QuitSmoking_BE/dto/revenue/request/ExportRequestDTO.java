package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for export request parameters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequestDTO {
    
    /**
     * Export format: CSV, EXCEL, PDF
     */
    private String format;
    
    /**
     * Export type: TRANSACTIONS, REVENUE, PLAN_STATS, USER_STATS
     */
    private String exportType;
    
    /**
     * Time period filter
     */
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    
    /**
     * Additional filters
     */
    private List<String> statuses;
    private List<Integer> planIds;
    private List<UUID> userIds;
    private String paymentMethod;
    
    /**
     * Export options
     */
    private boolean includeDetails;
    private String sortBy;
    private String sortDirection;
    
    /**
     * Pagination for large exports
     */
    private Integer maxRecords;
}
