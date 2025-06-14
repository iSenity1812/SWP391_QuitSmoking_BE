// src/main/java/com/swp391project.SWP391_QuitSmoking_BE/api/BlogController.java

package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.BlogRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.BlogResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.BlogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // <-- Import này là cần thiết cho Sort.Direction
import org.springframework.data.web.PageableDefault; // <-- Import này là cần thiết
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class BlogController {

    private final BlogService blogService;

    // --- ENDPOINT CÔNG KHAI CHO NGƯỜI DÙNG CUỐI (PUBLIC) ---

    // Lấy tất cả blog đã được PUBLISHED HOẶC tìm kiếm theo keyword (title/content), CÓ PHÂN TRANG
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getAllPublishedBlogs(
            @RequestParam(required = false) String keyword, // Từ khóa tìm kiếm cho tiêu đề HOẶC nội dung
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getAllPublishedBlogs(keyword, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog đã xuất bản thành công."));
    }

    // Lấy thông tin một blog cụ thể bằng ID (chỉ blog đã PUBLISHED)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> getBlogById(@PathVariable Integer id) {
        BlogResponseDTO blog = blogService.getBlogById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blog, "Lấy thông tin blog thành công."));
    }

    // --- ENDPOINT CHO THÀNH VIÊN (TẠO, SỬA, XÓA BLOG CỦA HỌ) ---

    // Tạo Blog: Yêu cầu quyền là thành viên (NORMAL_MEMBER, PREMIUM_MEMBER, COACH, CONTENT_ADMIN)
    @PostMapping
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> createBlog(
            @Valid @RequestBody BlogRequestDTO blogRequest,
            @AuthenticationPrincipal User currentUser) {
        BlogResponseDTO createdBlog = blogService.createBlog(blogRequest, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdBlog, "Tạo blog mới thành công."));
    }

    // Sửa Blog: Yêu cầu quyền là thành viên (NORMAL_MEMBER, PREMIUM_MEMBER, COACH, CONTENT_ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> updateBlog(
            @PathVariable Integer id,
            @Valid @RequestBody BlogRequestDTO blogRequest,
            @AuthenticationPrincipal User currentUser) {
        BlogResponseDTO updatedBlog = blogService.updateBlog(id, blogRequest, currentUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedBlog, "Cập nhật blog thành công."));
    }

    // Xóa Blog: Yêu cầu quyền là thành viên (NORMAL_MEMBER, PREMIUM_MEMBER, COACH, CONTENT_ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(
            @PathVariable Integer id,
            @AuthenticationPrincipal User currentUser) {
        blogService.deleteBlog(id, currentUser);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Xóa blog thành công."));
    }

    // --- ENDPOINT CHO QUẢN TRỊ VIÊN NỘI DUNG (CONTENT_ADMIN) ---

    // Duyệt Blog (chấp nhận): Chỉ Content Admin
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> approveBlog(
            @PathVariable Integer id,
            @AuthenticationPrincipal User adminUser) {
        BlogResponseDTO approvedBlog = blogService.approveBlog(id, adminUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(approvedBlog, "Duyệt blog thành công."));
    }

    // Từ chối Blog: Chỉ Content Admin
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> rejectBlog(
            @PathVariable Integer id,
            @AuthenticationPrincipal User adminUser,
            @RequestParam(required = false) String adminNotes) {
        BlogResponseDTO rejectedBlog = blogService.rejectBlog(id, adminUser, adminNotes);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(rejectedBlog, "Từ chối blog thành công."));
    }

    // Lấy tất cả blog (bao gồm PENDING, REJECTED) - chỉ dành cho Content Admin
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getAllBlogsForAdmin(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getAllBlogsIncludingPending(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách tất cả blog (Admin) thành công."));
    }

    // Lấy blog theo ID cho admin (cho phép xem cả bài chưa PUBLISHED)
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> getBlogByIdForAdmin(
            @PathVariable Integer id,
            @AuthenticationPrincipal User adminUser) {
        BlogResponseDTO blog = blogService.getBlogByIdForAdmin(id, adminUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blog, "Lấy thông tin blog (Admin) thành công."));
    }

    // --- NEW ADMIN FILTER/SEARCH ENDPOINTS ---

    // Admin: Lấy blog theo trạng thái cụ thể
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getBlogsByStatusForAdmin(
            @PathVariable BlogStatus status,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getBlogsByStatusForAdmin(status, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog theo trạng thái '" + status + "' thành công."));
    }

    // Admin: Lấy blog của một tác giả cụ thể
    @GetMapping("/admin/author/{authorId}")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getBlogsByAuthorForAdmin(
            @PathVariable UUID authorId,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getBlogsByAuthorForAdmin(authorId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog của tác giả '" + authorId + "' thành công."));
    }

    // Admin: Lấy blog theo trạng thái VÀ tác giả
    @GetMapping("/admin/status/{status}/author/{authorId}")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getBlogsByStatusAndAuthorForAdmin(
            @PathVariable BlogStatus status,
            @PathVariable UUID authorId,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getBlogsByStatusAndAuthorForAdmin(status, authorId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog theo trạng thái '" + status + "' và tác giả '" + authorId + "' thành công."));
    }
}