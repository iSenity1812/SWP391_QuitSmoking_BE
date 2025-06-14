package com.swp391project.SWP391_QuitSmoking_BE.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogSearchDTO {
    private String keyword;
    private String status; // Để admin tìm kiếm theo trạng thái
    private String authorName; // Tìm kiếm theo tên tác giả (cần logic phức tạp hơn trong service)
}