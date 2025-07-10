// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/dto/comment/CommentResponseDTO.java

package com.swp391project.SWP391_QuitSmoking_BE.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserResponseDTO; // Giả định bạn có UserResponseDTO
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
public class CommentResponseDTO {

    private Integer commentId;
    private String content;

    private Integer blogId; // ID của blog cha
    private UserResponseDTO user; // Thông tin người dùng bình luận

    private Integer parentCommentId; // ID của comment cha (nếu có)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentDate;

    private List<CommentResponseDTO> replies; // Các bình luận con (nếu có)
}