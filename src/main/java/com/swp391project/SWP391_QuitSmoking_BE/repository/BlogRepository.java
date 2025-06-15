// src/main/java/com/swp391project.SWP391_QuitSmoking_BE/repository/BlogRepository.java

package com.swp391project.SWP391_QuitSmoking_BE.repository; // Kiểm tra lại package này là 'repository' hay 'repositories'

import com.swp391project.SWP391_QuitSmoking_BE.entity.Blog;
import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus; // <-- Đảm bảo import enum này
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> { // Integer vì BlogID là int

    // Tìm kiếm theo tiêu đề HOẶC nội dung (có phân trang)
    Page<Blog> findByStatusAndTitleContainingIgnoreCaseOrStatusAndContentContainingIgnoreCase(
            BlogStatus statusForTitleSearch, String titleKeyword,
            BlogStatus statusForContentSearch, String contentKeyword,
            Pageable pageable);

    // Tìm kiếm blog của một tác giả cụ thể (có phân trang)
    Page<Blog> findByAuthor_UserId(UUID authorId, Pageable pageable);

    // Tìm kiếm blog theo trạng thái (có phân trang)
    Page<Blog> findByStatus(BlogStatus status, Pageable pageable); // <-- Thay đổi String thành BlogStatus

    // Tìm kiếm blog theo trạng thái VÀ tiêu đề (có phân trang)
    Page<Blog> findByStatusAndTitleContainingIgnoreCase(BlogStatus status, String titleKeyword, Pageable pageable); // <-- Thay đổi String thành BlogStatus

    // Thêm phương thức để tìm kiếm blog theo trạng thái VÀ của một tác giả cụ thể (có phân trang)
    Page<Blog> findByStatusAndAuthor_UserId(BlogStatus status, UUID authorId, Pageable pageable); // <-- Thay đổi String thành BlogStatus

    @Modifying // Báo hiệu đây là một truy vấn sửa đổi (UPDATE, DELETE, INSERT)
    @Query("UPDATE Blog b SET b.isDeleted = true WHERE b.blogId = :id") // JPQL: dùng tên thuộc tính của Entity
    void softDeleteById(@Param("id") Integer id);
}