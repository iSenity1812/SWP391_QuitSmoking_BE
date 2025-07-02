package com.swp391project.SWP391_QuitSmoking_BE.dto.coach;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QualificationResponseDTO {
    private String qualificationName;
    private String issuingOrganization;
    private String qualificationURL;
    private Boolean isApproved;
    private LocalDateTime requestUpdateDate;
    private String approveBy;
} 