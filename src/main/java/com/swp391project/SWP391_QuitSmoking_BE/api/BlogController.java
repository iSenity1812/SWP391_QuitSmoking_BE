package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.*;
import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.BlogRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.BlogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class BlogController {

    private final BlogService blogService;

    // --- ENDPOINT CÔNG KHAI CHO NGƯỜI DÙNG CUỐI (PUBLIC) ---

    // Lấy tất cả blog đã được PUBLISHED HOẶC tìm kiếm theo keyword (title/content), CÓ PHÂN TRANG
    @GetMapping // Endpoint này xử lý /api/blogs?keyword=... hoặc /api/blogs
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getAllPublishedBlogs(
            @RequestParam(required = false) String keyword,
            @PageableDefault(
                    page = 0,
                    size = 100,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getAllPublishedBlogs(keyword, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog đã xuất bản thành công."));
    }

    // Lấy thông tin một blog cụ thể bằng ID (chỉ blog đã PUBLISHED)
    // URL: /api/blogs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> getBlogById(@PathVariable Integer id) {
        BlogResponseDTO blog = blogService.getBlogById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blog, "Lấy thông tin blog thành công."));
    }

    // --- ENDPOINT CHO THÀNH VIÊN (TẠO, SỬA, XÓA BLOG CỦA HỌ) --

    // Tạo Blog: Hỗ trợ cả có và không có image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> createBlog(
            @Valid @ModelAttribute BlogRequestDTO blogRequest,
            @AuthenticationPrincipal User currentUser) {
        BlogResponseDTO createdBlog = blogService.createBlog(blogRequest, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdBlog, "Tạo blog mới thành công."));
    }

    // Sửa Blog: Hỗ trợ cả có và không có image
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> updateBlog(
            @PathVariable Integer id,
            @Valid @ModelAttribute BlogUpdateRequestDTO blogRequest,
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
                    size = 100,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getAllBlogsIncludingPending(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách tất cả blog (Admin) thành công."));
    }

    // Lấy blog theo ID cho admin (cho phép xem cả bài chưa PUBLISHED)
    // URL: /api/blogs/admin/{id}
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

    // --- NEW ADMIN FILTER/SEARCH ENDPOINTS (ĐÃ CÓ SẴN VÀ ĐÚNG) ---

    // Admin: Lấy blog theo trạng thái cụ thể
    // URL: /api/blogs/admin/status/{status}?page=...&size=...&sort=...
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getBlogsByStatusForAdmin(
            @PathVariable BlogStatus status, // <-- Ở đây bạn nhận BlogStatus enum trực tiếp
            @PageableDefault(
                    page = 0,
                    size = 100,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getBlogsByStatusForAdmin(status, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog theo trạng thái '" + status + "' thành công."));
    }

    // Admin: Lấy blog của một tác giả cụ thể
    // URL: /api/blogs/admin/author/{authorId}?page=...&size=...&sort=...
    @GetMapping("/admin/author/{authorId}")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getBlogsByAuthorForAdmin(
            @PathVariable UUID authorId,
            @PageableDefault(
                    page = 0,
                    size = 100,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getBlogsByAuthorForAdmin(authorId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog của tác giả '" + authorId + "' thành công."));
    }

    // Admin: Lấy blog theo trạng thái VÀ tác giả
    // URL: /api/blogs/admin/status/{status}/author/{authorId}?page=...&size=...&sort=...
    @GetMapping("/admin/status/{status}/author/{authorId}")
    @PreAuthorize("hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getBlogsByStatusAndAuthorForAdmin(
            @PathVariable BlogStatus status,
            @PathVariable UUID authorId,
            @PageableDefault(
                    page = 0,
                    size = 100,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getBlogsByStatusAndAuthorForAdmin(status, authorId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog theo trạng thái '" + status + "' và tác giả '" + authorId + "' thành công."));
    }

    @GetMapping("/my-blogs") // Ví dụ: /api/blogs/my-blogs
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogResponseDTO>>> getMyBlogs(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(page = 0, size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BlogResponseDTO> blogs = blogService.getBlogsByAuthor(currentUser.getUserId(), pageable); // Cần phương thức này trong BlogService
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy danh sách blog của tôi thành công."));
    }

    @GetMapping("/admin/statistics")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogStatisticsDTO>>> getAllBlogsForStatistics(
            @PageableDefault(
                    page = 0,
                    size = 100,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<BlogStatisticsDTO> blogs = blogService.getAllBlogsForStatistics(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy dữ liệu thống kê blog thành công."));
    }

    // Lấy tất cả blog cho statistics (không pagination - cẩn thận với data lớn)
    @GetMapping("/admin/statistics/all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<List<BlogStatisticsDTO>>> getAllBlogsForStatisticsNoPage() {
        List<BlogStatisticsDTO> blogs = blogService.getAllBlogsForStatistics();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy toàn bộ dữ liệu thống kê blog thành công."));
    }

    // Lấy blog statistics theo khoảng thời gian
    @GetMapping("/admin/statistics/date-range")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<List<BlogStatisticsDTO>>> getBlogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BlogStatisticsDTO> blogs = blogService.getBlogsForStatisticsByDateRange(startDate, endDate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy dữ liệu thống kê blog theo khoảng thời gian thành công."));
    }

    // Lấy blog statistics theo status
    @GetMapping("/admin/statistics/status/{status}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<List<BlogStatisticsDTO>>> getBlogsByStatus(
            @PathVariable BlogStatus status) {
        List<BlogStatisticsDTO> blogs = blogService.getBlogsForStatisticsByStatus(status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy dữ liệu thống kê blog theo trạng thái thành công."));
    }

    // Lấy blog statistics theo author
    @GetMapping("/admin/statistics/author/{authorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<List<BlogStatisticsDTO>>> getBlogsByAuthor(
            @PathVariable UUID authorId) {
        List<BlogStatisticsDTO> blogs = blogService.getBlogsForStatisticsByAuthor(authorId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(blogs, "Lấy dữ liệu thống kê blog theo tác giả thành công."));
    }

    // Lấy tóm tắt thống kê blog
    @GetMapping("/admin/statistics/summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogStatisticsSummaryDTO>> getBlogStatisticsSummary() {
        BlogStatisticsSummaryDTO summary = blogService.getBlogStatisticsSummary();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(summary, "Lấy tóm tắt thống kê blog thành công."));
    }

    // Lấy tóm tắt thống kê blog theo khoảng thời gian
    @GetMapping("/admin/statistics/summary/date-range")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<BlogStatisticsSummaryDTO>> getBlogStatisticsSummaryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BlogStatisticsSummaryDTO summary = blogService.getBlogStatisticsSummaryByDateRange(startDate, endDate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(summary, "Lấy tóm tắt thống kê blog theo khoảng thời gian thành công."));
    }
}
