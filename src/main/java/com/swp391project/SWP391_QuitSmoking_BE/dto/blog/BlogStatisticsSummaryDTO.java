package com.swp391project.SWP391_QuitSmoking_BE.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogStatisticsSummaryDTO {
    private Long totalBlogs;
    private Long publishedBlogs;
    private Long pendingBlogs;
    private Long rejectedBlogs;

    // Additional metrics
    private Long blogsThisMonth;
    private Long blogsThisWeek;
    private Long blogsToday;

    // Growth metrics
    private Double growthRate; // Tăng trưởng so với tháng trước
    private Long mostActiveAuthorId;
    private String mostActiveAuthorUsername;
}
