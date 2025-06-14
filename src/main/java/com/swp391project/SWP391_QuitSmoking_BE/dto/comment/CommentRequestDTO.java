package com.swp391project.SWP391_QuitSmoking_BE.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID; // Đảm bảo import UUID

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {

    @NotBlank(message = "Comment content cannot be empty")
    private String content;

    private Integer blogId; // ID của blog mà comment thuộc về
    private UUID userId; // ID của người dùng tạo comment (có thể lấy từ Principal thay vì gửi từ client)
    private Integer parentCommentId; // ID của comment cha nếu đây là comment trả lời
}