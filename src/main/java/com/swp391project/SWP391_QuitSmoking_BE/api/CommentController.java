    // src/main/java/com/swp391project.SWP391_QuitSmoking_BE/api/CommentController.java

    package com.swp391project.SWP391_QuitSmoking_BE.api;

    import com.swp391project.SWP391_QuitSmoking_BE.dto.comment.CommentRequestDTO;
    import com.swp391project.SWP391_QuitSmoking_BE.dto.comment.CommentResponseDTO;
    import com.swp391project.SWP391_QuitSmoking_BE.entity.User; // Đảm bảo import đúng User entity của bạn
    import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse; // Import ApiResponse
    import com.swp391project.SWP391_QuitSmoking_BE.service.CommentService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Import SecurityRequirement
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor; // Import RequiredArgsConstructor
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/comments")
    @RequiredArgsConstructor // Tự động inject CommentService thông qua constructor
    @SecurityRequirement(name = "user_api") // Yêu cầu token xác thực cho các endpoint
    public class CommentController {

        private final CommentService commentService;

        // Lấy tất cả comment gốc của một Blog (có phân trang)
        // Tương tự BlogController, có thể không cần @SecurityRequirement ở đây nếu là công khai
        @GetMapping("/blog/{blogId}")
        @Operation(security = { })
        public ResponseEntity<ApiResponse<Page<CommentResponseDTO>>> getCommentsByBlogId(
                @PathVariable Integer blogId,
                Pageable pageable) {
            Page<CommentResponseDTO> comments = commentService.getCommentsByBlogId(blogId, pageable);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(comments, "Lấy danh sách bình luận thành công."));
        }

        // Lấy một comment cụ thể theo ID
        @GetMapping("/{commentId}")
        public ResponseEntity<ApiResponse<CommentResponseDTO>> getCommentById(@PathVariable Integer commentId) {
            CommentResponseDTO comment = commentService.getCommentById(commentId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(comment, "Lấy thông tin bình luận thành công."));
        }

        // Thêm một comment mới (hoặc trả lời comment khác)
        @PostMapping
        @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
        public ResponseEntity<ApiResponse<CommentResponseDTO>> addComment(
                @Valid @RequestBody CommentRequestDTO commentRequest,
                @AuthenticationPrincipal User currentUser) {
            CommentResponseDTO newComment = commentService.addComment(commentRequest, currentUser);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(newComment, "Thêm bình luận mới thành công."));
        }

        // Xóa comment
        @DeleteMapping("/{commentId}")
        @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
        public ResponseEntity<ApiResponse<Void>> deleteComment(
                @PathVariable Integer commentId,
                @AuthenticationPrincipal User currentUser) {
            commentService.deleteComment(commentId, currentUser);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(ApiResponse.success(null, "Xóa bình luận thành công."));
        }
    }