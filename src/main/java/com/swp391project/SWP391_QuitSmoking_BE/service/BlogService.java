// src/main/java/com/swp391project.SWP391_QuitSmoking_BE/service/BlogService.java

package com.swp391project.SWP391_QuitSmoking_BE.service; // Kiểm tra lại package này là 'service' hay 'services'

import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.BlogRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.BlogResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Blog;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User; // Đảm bảo import User entity của bạn
import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus; // <-- Đảm bảo import enum này
import com.swp391project.SWP391_QuitSmoking_BE.repository.BlogRepository; // <-- Cập nhật import Repository
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper;
    // Có thể cần UserRepository nếu bạn muốn kiểm tra quyền tác giả chặt chẽ hơn.
    // private final UserRepository userRepository;

    // --- Phương thức công khai cho người dùng cuối (tìm kiếm và phân trang) ---
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getAllPublishedBlogs(String keyword, Pageable pageable) {
        Page<Blog> blogsPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // SỬ DỤNG PHƯƠNG THỨC MỚI CỦA REPOSITORY
            blogsPage = blogRepository.findByStatusAndTitleContainingIgnoreCaseOrStatusAndContentContainingIgnoreCase(
                    BlogStatus.PUBLISHED, keyword,    // Tìm kiếm trong tiêu đề, trạng thái PUBLISHED
                    BlogStatus.PUBLISHED, keyword,    // Tìm kiếm trong nội dung, trạng thái PUBLISHED
                    pageable
            );
        } else {
            // Nếu không có keyword, lấy tất cả blog đã PUBLISHED với phân trang
            blogsPage = blogRepository.findByStatus(BlogStatus.PUBLISHED, pageable);
        }
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Lấy thông tin một blog cụ thể bằng ID (công khai)
    @Transactional(readOnly = true)
    public BlogResponseDTO getBlogById(Integer blogId) {
        Blog blog = blogRepository.findById(blogId)
                .filter(b -> b.getStatus() == BlogStatus.PUBLISHED) // Chỉ trả về nếu đã PUBLISHED
                .orElseThrow(() -> new EntityNotFoundException("Blog không tìm thấy hoặc chưa được xuất bản với ID: " + blogId));
        return convertToBlogResponseDTO(blog);
    }

    // --- Phương thức cho người tạo Blog (thành viên) ---
    @Transactional
    public BlogResponseDTO createBlog(BlogRequestDTO blogRequest, User currentUser) {
        Blog newBlog = modelMapper.map(blogRequest, Blog.class);
        newBlog.setAuthor(currentUser); // Gán tác giả là người dùng hiện tại
        newBlog.setCreatedAt(LocalDateTime.now());
        newBlog.setLastUpdated(LocalDateTime.now());
        newBlog.setStatus(BlogStatus.PENDING); // Mặc định là PENDING khi tạo mới
        newBlog = blogRepository.save(newBlog);
        return convertToBlogResponseDTO(newBlog);
    }

    @Transactional
    public BlogResponseDTO updateBlog(Integer id, BlogRequestDTO blogRequest, User currentUser) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog không tìm thấy với ID: " + id));

        // Kiểm tra quyền: chỉ tác giả hoặc CONTENT_ADMIN mới được sửa
        if (!existingBlog.getAuthor().getUserId().equals(currentUser.getUserId()) && !currentUser.getRole().equals("CONTENT_ADMIN")) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền sửa blog này.");
        }

        modelMapper.map(blogRequest, existingBlog); // Cập nhật các trường từ DTO
        existingBlog.setLastUpdated(LocalDateTime.now());
        // Nếu blog đang PUBLISHED, khi tác giả sửa, có thể chuyển về PENDING để admin duyệt lại
        // Hoặc giữ nguyên trạng thái nếu quyền admin sửa. Tùy thuộc vào quy tắc nghiệp vụ.
        // Ví dụ: Nếu tác giả sửa và không phải admin, chuyển về PENDING
        if (!currentUser.getRole().equals("CONTENT_ADMIN") && existingBlog.getStatus() == BlogStatus.PUBLISHED) {
            existingBlog.setStatus(BlogStatus.PENDING);
        }
        existingBlog = blogRepository.save(existingBlog);
        return convertToBlogResponseDTO(existingBlog);
    }

    @Transactional
    public void deleteBlog(Integer id, User currentUser) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog không tìm thấy với ID: " + id));

        // Kiểm tra quyền: chỉ tác giả hoặc CONTENT_ADMIN mới được xóa
        if (!existingBlog.getAuthor().getUserId().equals(currentUser.getUserId()) && !currentUser.getRole().equals("CONTENT_ADMIN")) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền xóa blog này.");
        }
        blogRepository.delete(existingBlog);
    }

    // --- Phương thức cho Admin (duyệt, từ chối, lấy tất cả blog) ---

    @Transactional
    public BlogResponseDTO approveBlog(Integer id, User adminUser) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog không tìm thấy với ID: " + id));
        if (blog.getStatus() != BlogStatus.PUBLISHED) { // Chỉ duyệt blog chưa PUBLISHED
            blog.setStatus(BlogStatus.PUBLISHED);
            blog.setLastUpdated(LocalDateTime.now());
            // Có thể thêm logic lưu adminId hoặc thời gian duyệt
            blog = blogRepository.save(blog);
        }
        return convertToBlogResponseDTO(blog);
    }

    @Transactional
    public BlogResponseDTO rejectBlog(Integer id, User adminUser, String adminNotes) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog không tìm thấy với ID: " + id));
        if (blog.getStatus() != BlogStatus.REJECTED) { // Chỉ từ chối blog chưa REJECTED
            blog.setStatus(BlogStatus.REJECTED);
            blog.setLastUpdated(LocalDateTime.now());
            blog = blogRepository.save(blog);
        }
        return convertToBlogResponseDTO(blog);
    }

    // Lấy tất cả blog (bao gồm PENDING, REJECTED) cho Admin
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getAllBlogsIncludingPending(Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findAll(pageable); // Lấy tất cả blog bất kể trạng thái
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Lấy blog theo ID cho admin (có thể xem cả bài chưa PUBLISHED)
    @Transactional(readOnly = true)
    public BlogResponseDTO getBlogByIdForAdmin(Integer id, User adminUser) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog không tìm thấy với ID: " + id));
        return convertToBlogResponseDTO(blog);
    }

    // --- NEW ADMIN SEARCH METHODS ---

    // Admin: Lấy blog theo trạng thái cụ thể
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByStatusForAdmin(BlogStatus status, Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findByStatus(status, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Admin: Lấy blog của một tác giả cụ thể
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByAuthorForAdmin(UUID authorId, Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findByAuthor_UserId(authorId, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Admin: Lấy blog theo trạng thái VÀ tác giả
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByStatusAndAuthorForAdmin(BlogStatus status, UUID authorId, Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findByStatusAndAuthor_UserId(status, authorId, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Helper method để chuyển đổi Entity sang DTO
    private BlogResponseDTO convertToBlogResponseDTO(Blog blog) {
        // Đảm bảo BlogResponseDTO có các trường cần thiết và modelMapper đã được cấu hình đúng.
        return modelMapper.map(blog, BlogResponseDTO.class);
    }
}