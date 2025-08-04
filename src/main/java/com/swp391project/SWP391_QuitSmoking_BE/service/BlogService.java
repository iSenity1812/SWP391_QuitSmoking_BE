package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.*;
import com.swp391project.SWP391_QuitSmoking_BE.dto.comment.CommentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Blog;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.exception.AppException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ErrorCode;
import com.swp391project.SWP391_QuitSmoking_BE.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper;
    private final CommentService commentService;
    private final FileUploadService fileUploadService;

    // Helper method để chuyển đổi Entity sang DTO (sử dụng ModelMapper)
    private BlogResponseDTO convertToBlogResponseDTO(Blog blog) {
        if (blog == null) return null;

        try {
            // Tạo DTO thủ công để đảm bảo mapping chính xác
            BlogResponseDTO dto = new BlogResponseDTO();
            
            // Map các field cơ bản
            dto.setBlogId(blog.getBlogId());
            dto.setTitle(blog.getTitle());
            dto.setContent(blog.getContent());
            dto.setImageUrl(blog.getImageUrl());
            dto.setStatus(blog.getStatus() != null ? blog.getStatus().toString() : null);
            dto.setCreatedAt(blog.getCreatedAt());
            dto.setLastUpdated(blog.getLastUpdated());
            dto.setApprovedAt(blog.getApprovedAt());
            
            // Map author information
            if (blog.getAuthor() != null) {
                dto.setAuthor(new com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserResponseDTO());
                dto.getAuthor().setUserId(blog.getAuthor().getUserId());
                dto.getAuthor().setUsername(blog.getAuthor().getUsername());
                dto.getAuthor().setName(blog.getAuthor().getUsername()); // Use username as name since User doesn't have name field
                dto.getAuthor().setEmail(blog.getAuthor().getEmail());
            }
            
            // Map approvedBy information
            if (blog.getApprovedBy() != null) {
                dto.setApprovedBy(new com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserResponseDTO());
                dto.getApprovedBy().setUserId(blog.getApprovedBy().getUserId());
                dto.getApprovedBy().setUsername(blog.getApprovedBy().getUsername());
                dto.getApprovedBy().setName(blog.getApprovedBy().getUsername()); // Use username as name since User doesn't have name field
            }

            // BỔ SUNG QUAN TRỌNG: Ánh xạ comments vào BlogResponseDTO
            if (blog.getComments() != null && !blog.getComments().isEmpty()) {
                try {
                    // Đảm bảo convertToDtoWithReplies trong CommentService là public và KHÔNG static
                    List<CommentResponseDTO> commentDTOs = blog.getComments().stream()
                            .map(commentService::convertToDtoWithReplies)
                            .collect(Collectors.toList());
                    dto.setComments(commentDTOs);
                    dto.setCommentCount(commentDTOs.size());
                } catch (Exception e) {
                    // Nếu có lỗi khi xử lý comments, set empty list
                    System.err.println("Error processing comments for blog " + blog.getBlogId() + ": " + e.getMessage());
                    dto.setComments(Collections.emptyList());
                    dto.setCommentCount(0);
                }
            } else {
                dto.setComments(Collections.emptyList());
                dto.setCommentCount(0);
            }

            // Debug logging
            System.out.println("Successfully converted Blog entity to DTO: blogId=" + dto.getBlogId() + ", title=" + dto.getTitle());
            
            return dto;
        } catch (Exception e) {
            // Log lỗi và trả về null
            System.err.println("Error converting blog to DTO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByAuthor(UUID authorId, Pageable pageable) {
        Page<Blog> blogs = blogRepository.findByAuthor_UserId(authorId, pageable);
        return blogs.map(this::convertToBlogResponseDTO);
    }

    // --- Phương thức công khai cho người dùng cuối (tìm kiếm và phân trang) ---
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getAllPublishedBlogs(String keyword, Pageable pageable) {
        Page<Blog> blogsPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            blogsPage = blogRepository.findByStatusAndTitleContainingIgnoreCaseOrStatusAndContentContainingIgnoreCase(
                    BlogStatus.PUBLISHED, keyword,
                    BlogStatus.PUBLISHED, keyword,
                    pageable
            );
        } else {
            blogsPage = blogRepository.findByStatus(BlogStatus.PUBLISHED, pageable);
        }
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Lấy thông tin một blog cụ thể bằng ID (công khai)
    @Transactional(readOnly = true)
    public BlogResponseDTO getBlogById(Integer blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        
        // Kiểm tra status sau khi tìm thấy blog
        if (blog.getStatus() != BlogStatus.PUBLISHED) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        
        return convertToBlogResponseDTO(blog);
    }

    // --- Phương thức tạo blog gộp chung (có thể có hoặc không có image) ---
    @Transactional
    public BlogResponseDTO createBlog(BlogRequestDTO blogRequest, User currentUser) {
        Blog newBlog = new Blog();
        newBlog.setTitle(blogRequest.getTitle());
        newBlog.setContent(blogRequest.getContent());
        newBlog.setAuthor(currentUser);
        newBlog.setCreatedAt(LocalDateTime.now());
        newBlog.setLastUpdated(LocalDateTime.now());
        newBlog.setDeleted(false);

        // Upload image nếu có
        if (blogRequest.getImageUrl() != null && !blogRequest.getImageUrl().isEmpty()) {
            String imageUrl = fileUploadService.uploadImage(blogRequest.getImageUrl());
            newBlog.setImageUrl(imageUrl);
        }

        // Đặt trạng thái dựa trên vai trò
        Role userRole = currentUser.getRole();
        if (userRole == Role.NORMAL_MEMBER || userRole == Role.PREMIUM_MEMBER ||
                userRole == Role.SUPER_ADMIN || userRole == Role.CONTENT_ADMIN) {
            newBlog.setStatus(BlogStatus.PUBLISHED);
        } else if (userRole == Role.COACH) {
            newBlog.setStatus(BlogStatus.PENDING);
        } else {
            newBlog.setStatus(BlogStatus.PENDING);
        }

        newBlog = blogRepository.save(newBlog);
        return convertToBlogResponseDTO(newBlog);
    }

    // Helper method để kiểm tra MultipartFile có hợp lệ không
    private boolean isValidImageFile(org.springframework.web.multipart.MultipartFile file) {
        return file != null && !file.isEmpty() && file.getSize() > 0;
    }

    // --- Phương thức cập nhật blog gộp chung (có thể có hoặc không có image) ---
    @Transactional
    public BlogResponseDTO updateBlog(Integer id, BlogUpdateRequestDTO blogRequest, User currentUser) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        if (existingBlog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_DELETED);
        }

        // Kiểm tra quyền
        boolean isAuthor = existingBlog.getAuthor().getUserId().equals(currentUser.getUserId());
        if (!isAuthor) {
            throw new AccessDeniedException("Bạn không có quyền sửa blog này.");
        }

        // Lưu URL image cũ để xóa nếu cần
        String oldImageUrl = existingBlog.getImageUrl();

        // Cập nhật thông tin blog
        existingBlog.setTitle(blogRequest.getTitle());
        existingBlog.setContent(blogRequest.getContent());
        existingBlog.setLastUpdated(LocalDateTime.now());

        // Xử lý image với logic rõ ràng hơn
        boolean shouldRemoveImage = blogRequest.getRemoveImage() != null && blogRequest.getRemoveImage();
        boolean hasNewImage = isValidImageFile(blogRequest.getImageUrl());

        if (shouldRemoveImage) {
            // Trường hợp 1: Người dùng muốn xóa image hiện tại
            if (oldImageUrl != null) {
                fileUploadService.deleteImage(oldImageUrl);
            }
            existingBlog.setImageUrl(null);
        } else if (hasNewImage) {
            // Trường hợp 2: Người dùng muốn upload image mới
            String newImageUrl = fileUploadService.uploadImage(blogRequest.getImageUrl());
            existingBlog.setImageUrl(newImageUrl);

            // Xóa image cũ nếu có
            if (oldImageUrl != null) {
                fileUploadService.deleteImage(oldImageUrl);
            }
        }
        // Trường hợp 3: Không có removeImage flag và không có image mới
        // → Giữ nguyên image cũ (không làm gì cả)

        // Cập nhật trạng thái nếu cần
        if (existingBlog.getStatus() == BlogStatus.PUBLISHED || existingBlog.getStatus() == BlogStatus.REJECTED) {
            existingBlog.setStatus(BlogStatus.PUBLISHED);
        }

        existingBlog = blogRepository.save(existingBlog);
        return convertToBlogResponseDTO(existingBlog);
    }

    // Xóa Blog (Soft Delete)
    @Transactional
    public void deleteBlog(Integer id, User currentUser) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        if (existingBlog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_DELETED);
        }

        boolean isAuthor = existingBlog.getAuthor().getUserId().equals(currentUser.getUserId());
        Role userRole = currentUser.getRole();
        boolean isContentAdmin = (userRole == Role.CONTENT_ADMIN);

        if (!isAuthor && !isContentAdmin) {
            throw new AccessDeniedException("Bạn không có quyền xóa blog này.");
        }

        blogRepository.softDeleteById(id);
    }

    // --- Phương thức cho Admin (duyệt, từ chối, lấy tất cả blog) ---

    @Transactional
    public BlogResponseDTO approveBlog(Integer id, User adminUser) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        if (blog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_DELETED);
        }

        if (blog.getStatus() == BlogStatus.PENDING || blog.getStatus() == BlogStatus.REJECTED) {
            blog.setStatus(BlogStatus.PUBLISHED);
            blog.setLastUpdated(LocalDateTime.now());
            blog = blogRepository.save(blog);
        }
        return convertToBlogResponseDTO(blog);
    }

    @Transactional
    public BlogResponseDTO rejectBlog(Integer id, User adminUser, String adminNotes) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        if (blog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_DELETED);
        }

        if (blog.getStatus() == BlogStatus.PENDING || blog.getStatus() == BlogStatus.PUBLISHED) {
            blog.setStatus(BlogStatus.REJECTED);
            blog.setLastUpdated(LocalDateTime.now());
            blog = blogRepository.save(blog);
        }
        return convertToBlogResponseDTO(blog);
    }

    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getAllBlogsIncludingPending(Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findAll(pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    @Transactional(readOnly = true)
    public BlogResponseDTO getBlogByIdForAdmin(Integer id, User adminUser) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        if (blog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        return convertToBlogResponseDTO(blog);
    }

    // --- NEW ADMIN SEARCH METHODS ---

    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByStatusForAdmin(BlogStatus status, Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findByStatus(status, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByAuthorForAdmin(UUID authorId, Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findByAuthor_UserId(authorId, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByStatusAndAuthorForAdmin(BlogStatus status, UUID authorId, Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findByStatusAndAuthor_UserId(status, authorId, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getMyBlogs(User currentUser, Pageable pageable) {
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Page<Blog> blogsPage = blogRepository.findByAuthor_UserId(currentUser.getUserId(), pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<BlogStatisticsDTO> getAllBlogsForStatistics() {
        return blogRepository.findAllBlogsForStatistics();
    }

    @Transactional(readOnly = true)
    public Page<BlogStatisticsDTO> getAllBlogsForStatistics(Pageable pageable) {
        return blogRepository.findAllBlogsForStatistics(pageable);
    }

    @Transactional(readOnly = true)
    public List<BlogStatisticsDTO> getBlogsForStatisticsByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return blogRepository.findBlogsForStatisticsByDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<BlogStatisticsDTO> getBlogsForStatisticsByStatus(BlogStatus status) {
        return blogRepository.findBlogsForStatisticsByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<BlogStatisticsDTO> getBlogsForStatisticsByAuthor(UUID authorId) {
        return blogRepository.findBlogsForStatisticsByAuthor(authorId);
    }

    @Transactional(readOnly = true)
    public BlogStatisticsSummaryDTO getBlogStatisticsSummary() {
        Long totalBlogs = blogRepository.count();
        Long publishedBlogs = blogRepository.countBlogsByStatus(BlogStatus.PUBLISHED);
        Long pendingBlogs = blogRepository.countBlogsByStatus(BlogStatus.PENDING);
        Long rejectedBlogs = blogRepository.countBlogsByStatus(BlogStatus.REJECTED);

        return BlogStatisticsSummaryDTO.builder()
                .totalBlogs(totalBlogs)
                .publishedBlogs(publishedBlogs)
                .pendingBlogs(pendingBlogs)
                .rejectedBlogs(rejectedBlogs)
                .build();
    }

    @Transactional(readOnly = true)
    public BlogStatisticsSummaryDTO getBlogStatisticsSummaryByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate) {
        Long totalBlogs = blogRepository.countBlogsByDateRange(startDate, endDate);

        // Có thể thêm các query phức tạp hơn để đếm theo status trong date range
        return BlogStatisticsSummaryDTO.builder()
                .totalBlogs(totalBlogs)
                .build();
    }
}
