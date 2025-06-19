// src/main/java/com/swp391project.SWP391_QuitSmoking_BE/service/BlogService.java

package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.BlogRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.BlogResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Blog;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.exception.AppException; // Đảm bảo import này
import com.swp391project.SWP391_QuitSmoking_BE.exception.ErrorCode; // Đảm bảo import này
import com.swp391project.SWP391_QuitSmoking_BE.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper; // Đảm bảo import này
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException; // Đảm bảo import này
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID; // Dùng cho User ID nếu là UUID

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper; // Đảm bảo đã inject ModelMapper

    // Helper method để chuyển đổi Entity sang DTO (sử dụng ModelMapper)
    private BlogResponseDTO convertToBlogResponseDTO(Blog blog) {
        if (blog == null) return null;
        // Đảm bảo ModelMapper của bạn đã được cấu hình để map Blog entity
        // sang BlogResponseDTO, bao gồm cả author.id thành authorId và author.username thành authorUsername.
        return modelMapper.map(blog, BlogResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByAuthor(UUID authorId, Pageable pageable) {
        // Tìm kiếm các bài blog thuộc về tác giả có authorId được chỉ định.
        // Giả định entity Blog có trường 'author' là một quan hệ tới entity User.
        // Spring Data JPA sẽ tự động tạo phương thức này nếu bạn tuân thủ quy tắc đặt tên.
        // `findByAuthor_UserId` nghĩa là "tìm kiếm Blog nơi trường 'author' có 'userId' bằng với authorId đã cho."
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
                .filter(b -> b.getStatus() == BlogStatus.PUBLISHED)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND)); // Sử dụng AppException
        return convertToBlogResponseDTO(blog);
    }

    // --- Phương thức cho người tạo Blog (thành viên) ---
    @Transactional
    public BlogResponseDTO createBlog(BlogRequestDTO blogRequest, User currentUser) {
        Blog newBlog = modelMapper.map(blogRequest, Blog.class);
        newBlog.setAuthor(currentUser);
        newBlog.setCreatedAt(LocalDateTime.now());
        newBlog.setLastUpdated(LocalDateTime.now());
        newBlog.setDeleted(false);

        // --- ĐIỀU CHỈNH LỚN Ở ĐÂY ĐỂ ĐẶT TRẠNG THÁI DỰA TRÊN VAI TRÒ ---
        Role userRole = currentUser.getRole();

        if (userRole == Role.NORMAL_MEMBER || userRole == Role.PREMIUM_MEMBER || userRole == Role.SUPER_ADMIN || userRole == Role.CONTENT_ADMIN) {
            newBlog.setStatus(BlogStatus.PUBLISHED); // Các vai trò này được xuất bản ngay lập tức
        } else if (userRole == Role.COACH) {
            newBlog.setStatus(BlogStatus.PENDING); // Coach cần duyệt
        } else {
            // Đối với các vai trò khác không mong muốn tạo blog, bạn có thể ném lỗi hoặc đặt mặc định PENDING
            // Ví dụ: throw new AccessDeniedException("Vai trò của bạn không được phép tạo blog.");
            newBlog.setStatus(BlogStatus.PENDING); // Mặc định là PENDING nếu không khớp vai trò nào
        }
        // --- KẾT THÚC ĐIỀU CHỈNH ---

        newBlog = blogRepository.save(newBlog);
        return convertToBlogResponseDTO(newBlog);
    }

    @Transactional
    public BlogResponseDTO updateBlog(Integer id, BlogRequestDTO blogRequest, User currentUser) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND)); // Sử dụng AppException

        // BƯỚC SỬA LỖI QUAN TRỌNG: Ngăn cập nhật nếu blog đã bị xóa mềm
        if (existingBlog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_DELETED); // Ném lỗi cụ thể hơn
        }

        // Kiểm tra quyền: chỉ tác giả HOẶC ADMIN HOẶC CONTENT_ADMIN mới được sửa
        boolean isAuthor = existingBlog.getAuthor().getUserId().equals(currentUser.getUserId()); // Giả sử User ID là UUID và getter là getId()
        Role userRole = currentUser.getRole();
        boolean isContentAdmin = (userRole == Role.CONTENT_ADMIN);

        if (!isAuthor && !isContentAdmin) {
            throw new AccessDeniedException("Bạn không có quyền sửa blog này.");
        }

        modelMapper.map(blogRequest, existingBlog);
        existingBlog.setLastUpdated(LocalDateTime.now()); // Chuẩn hóa tên trường

        // Nếu blog đang PUBLISHED hoặc REJECTED, khi tác giả sửa, chuyển về PENDING để admin duyệt lại
        // Admin (ADMIN/CONTENT_ADMIN) có thể sửa mà không chuyển về PENDING
        if (!isContentAdmin && (existingBlog.getStatus() == BlogStatus.PUBLISHED || existingBlog.getStatus() == BlogStatus.REJECTED)) {
            existingBlog.setStatus(BlogStatus.PENDING);
        }

        existingBlog = blogRepository.save(existingBlog);
        return convertToBlogResponseDTO(existingBlog);
    }

    // Xóa Blog (Soft Delete)
    @Transactional
    public void deleteBlog(Integer id, User currentUser) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND)); // Sử dụng AppException

        // Kiểm tra nếu blog đã bị xóa mềm rồi
        if (existingBlog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_DELETED); // Ném lỗi cụ thể hơn
        }

        boolean isAuthor = existingBlog.getAuthor().getUserId().equals(currentUser.getUserId()); // Giả sử User ID là UUID và getter là getId()
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
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND)); // Sử dụng AppException

        // Kiểm tra nếu blog đã bị xóa mềm
        if (blog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_DELETED); // Admin không thể duyệt blog đã xóa
        }

        // Chỉ duyệt blog đang PENDING hoặc REJECTED
        if (blog.getStatus() == BlogStatus.PENDING || blog.getStatus() == BlogStatus.REJECTED) {
            blog.setStatus(BlogStatus.PUBLISHED);
            blog.setLastUpdated(LocalDateTime.now()); // Chuẩn hóa tên trường
            // Có thể thêm logic lưu adminId hoặc thời gian duyệt
            blog = blogRepository.save(blog);
        } else {
            // Có thể ném lỗi nếu muốn ngăn duyệt một blog đã PUBLISHED
            // throw new AppException(ErrorCode.BLOG_ALREADY_PUBLISHED);
        }
        return convertToBlogResponseDTO(blog);
    }

    @Transactional
    public BlogResponseDTO rejectBlog(Integer id, User adminUser, String adminNotes) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND)); // Sử dụng AppException

        // Kiểm tra nếu blog đã bị xóa mềm
        if (blog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_DELETED); // Admin không thể từ chối blog đã xóa
        }

        // Chỉ từ chối blog đang PENDING hoặc PUBLISHED
        if (blog.getStatus() == BlogStatus.PENDING || blog.getStatus() == BlogStatus.PUBLISHED) {
            blog.setStatus(BlogStatus.REJECTED);
//            blog.setAdminNotes(adminNotes); // Lưu ghi chú từ admin
            blog.setLastUpdated(LocalDateTime.now()); // Chuẩn hóa tên trường
            blog = blogRepository.save(blog);
        } else {
            // Có thể ném lỗi nếu muốn ngăn từ chối một blog đã REJECTED
            // throw new AppException(ErrorCode.BLOG_ALREADY_REJECTED);
        }
        return convertToBlogResponseDTO(blog);
    }

    // Lấy tất cả blog (bao gồm PENDING, REJECTED, nhưng không bị xóa mềm) cho Admin
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getAllBlogsIncludingPending(Pageable pageable) {
        // Nếu Blog entity có @Where(clause = "is_deleted = false") thì findAll() đã tự lọc.
        // Nếu không, bạn cần một phương thức findByIsDeletedFalse() trong repository.
        Page<Blog> blogsPage = blogRepository.findAll(pageable); // Lấy tất cả blog bất kể trạng thái (nhưng không phải đã xóa mềm)
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Lấy blog theo ID cho admin (có thể xem cả bài chưa PUBLISHED, nhưng không phải đã xóa mềm)
    @Transactional(readOnly = true)
    public BlogResponseDTO getBlogByIdForAdmin(Integer id, User adminUser) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND)); // Sử dụng AppException

        // Admin cũng không nên xem blog đã bị xóa mềm qua route này (trừ khi có route riêng cho việc khôi phục)
        if (blog.isDeleted()) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND); // Coi như không tồn tại
        }
        return convertToBlogResponseDTO(blog);
    }

    // --- NEW ADMIN SEARCH METHODS ---

    // Admin: Lấy blog theo trạng thái cụ thể (không bị xóa mềm)
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByStatusForAdmin(BlogStatus status, Pageable pageable) {
        Page<Blog> blogsPage = blogRepository.findByStatus(status, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Admin: Lấy blog của một tác giả cụ thể (không bị xóa mềm)
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByAuthorForAdmin(UUID authorId, Pageable pageable) {
        // Đảm bảo BlogRepository có phương thức này: Page<Blog> findByAuthor_Id(UUID authorId, Pageable pageable);
        Page<Blog> blogsPage = blogRepository.findByAuthor_UserId(authorId, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Admin: Lấy blog theo trạng thái VÀ tác giả (không bị xóa mềm)
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getBlogsByStatusAndAuthorForAdmin(BlogStatus status, UUID authorId, Pageable pageable) {
        // Đảm bảo BlogRepository có phương thức này: Page<Blog> findByStatusAndAuthor_Id(BlogStatus status, UUID authorId, Pageable pageable);
        Page<Blog> blogsPage = blogRepository.findByStatusAndAuthor_UserId(status, authorId, pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }

    // Phương thức mới: Lấy tất cả bài viết của người dùng hiện tại
    @Transactional(readOnly = true)
    public Page<BlogResponseDTO> getMyBlogs(User currentUser, Pageable pageable) {
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        // Giả sử User.getId() trả về UUID
        Page<Blog> blogsPage = blogRepository.findByAuthor_UserId(currentUser.getUserId(), pageable);
        return blogsPage.map(this::convertToBlogResponseDTO);
    }
}