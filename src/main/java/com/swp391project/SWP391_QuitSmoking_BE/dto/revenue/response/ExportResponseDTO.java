package com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for export response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponseDTO {
    
    /**
     * Export job ID for tracking
     */
    private String exportId;
    
    /**
     * File name
     */
    private String fileName;
    
    /**
     * File size in bytes
     */
    private Long fileSize;
    
    /**
     * Number of records exported
     */
    private Long recordCount;
    
    /**
     * Download URL or file path
     */
    private String downloadUrl;
    
    /**
     * Export status: PROCESSING, COMPLETED, FAILED
     */
    private String status;
    
    /**
     * Export completion time
     */
    private String completedAt;
    
    /**
     * Error message if failed
     */
    private String errorMessage;
}
