package com.swp391project.SWP391_QuitSmoking_BE.dto.blog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDTO {

    private Long blogId;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String category;
    private String tags;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPublished;
    private int viewCount;
    private int likeCount;
    private int commentCount;
}