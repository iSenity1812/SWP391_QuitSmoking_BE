// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/repositories/CommentRepository.java

package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID; // Đảm bảo import UUID nếu UserID là UUID

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> { // Integer vì CommentID là int

    List<Comment> findByBlog_BlogId(Integer blogId);

    Page<Comment> findByBlog_BlogId(Integer blogId, Pageable pageable);

    Page<Comment> findByBlog_BlogIdAndParentCommentIsNull(Integer blogId, Pageable pageable);

    Page<Comment> findByParentComment_CommentId(Integer parentCommentId, Pageable pageable);

    Page<Comment> findByUser_UserId(UUID userId, Pageable pageable); // Dùng UUID nếu UserID của User là UUID

    long countByBlog_BlogId(Integer blogId);

    // Phương thức soft delete comment
    @Modifying
    @Query("UPDATE Comment c SET c.isDeleted = true WHERE c.commentId = :id")
    void softDeleteById(@Param("id") Integer id);
}