package com.swp391project.SWP391_QuitSmoking_BE.dto.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.comment.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponseDTO {

    private Integer blogId;
    private String title;
    private String content;
    private String imageUrl;
    private Boolean removeImage;

    private UserResponseDTO author; // Thông tin tác giả (chỉ cần id, name, etc.)
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    private UserResponseDTO approvedBy; // Thông tin người duyệt (nếu có)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;

    private List<CommentResponseDTO> comments; // Không nên tải tất cả comments ở đây để tránh quá tải
    // Thường chỉ đếm số lượng hoặc tải riêng
    private long commentCount; // Số lượng bình luận

    // Có thể thêm viewsCount nếu bạn có trường đó trong Entity
    // private Integer viewsCount;
}
