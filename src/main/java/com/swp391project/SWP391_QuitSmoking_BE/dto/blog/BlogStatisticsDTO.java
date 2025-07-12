package com.swp391project.SWP391_QuitSmoking_BE.dto.blog;

import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogStatisticsDTO {
    private Integer blogId;
    private LocalDateTime createdAt;
    private BlogStatus status;

    // Author information
    private UUID authorId;
    private String authorUsername;
    private String authorEmail;

    // Additional field for statistics
    private boolean isDeleted;
}