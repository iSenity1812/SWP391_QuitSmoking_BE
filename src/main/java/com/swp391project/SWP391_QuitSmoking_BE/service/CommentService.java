// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/services/CommentService.java

package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.comment.CommentRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.comment.CommentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Blog;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Comment;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.repository.BlogRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CommentRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CommentService(CommentRepository commentRepository, BlogRepository blogRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CommentResponseDTO addComment(CommentRequestDTO commentRequest, User currentUser) {
        Blog blog = blogRepository.findById(commentRequest.getBlogId())
                .orElseThrow(() -> new EntityNotFoundException("Blog not found with ID: " + commentRequest.getBlogId()));

        if (blog.getStatus() != BlogStatus.PUBLISHED) {
            throw new IllegalArgumentException("Cannot add comment to a non-published blog.");
        }

        User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        Comment comment = modelMapper.map(commentRequest, Comment.class);
        comment.setBlog(blog);
        comment.setUser(user);
        comment.setCommentDate(LocalDateTime.now());

        if (commentRequest.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentRequest.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found with ID: " + commentRequest.getParentCommentId()));
            comment.setParentComment(parentComment);
        }

        comment = commentRepository.save(comment);
        return convertToDtoWithReplies(comment);
    }

    public Page<CommentResponseDTO> getCommentsByBlogId(Integer blogId, Pageable pageable) {
        Page<Comment> commentsPage = commentRepository.findByBlog_BlogIdAndParentCommentIsNull(blogId, pageable);
        return commentsPage.map(this::convertToDtoWithReplies);
    }

    public CommentResponseDTO getCommentById(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));
        return convertToDtoWithReplies(comment);
    }

    @Transactional
    public void deleteComment(Integer commentId, User currentUser) {
        Comment commentToDelete = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // --- ĐIỀU CHỈNH DÒNG NÀY ---
        boolean isAdmin = (currentUser.getRole() == Role.CONTENT_ADMIN); // <-- Sửa lỗi ở đây
        // -------------------------

        boolean isCommentOwner = commentToDelete.getUser().getUserId().equals(currentUser.getUserId());
        boolean isBlogAuthor = commentToDelete.getBlog().getAuthor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isCommentOwner && !isBlogAuthor) {
            throw new AccessDeniedException("You are not authorized to delete this comment.");
        }

        commentRepository.delete(commentToDelete);
    }

    private CommentResponseDTO convertToDtoWithReplies(Comment comment) {
        CommentResponseDTO dto = modelMapper.map(comment, CommentResponseDTO.class);
        if (comment.getUser() != null) {
            dto.setUser(modelMapper.map(comment.getUser(), UserResponseDTO.class));
        }
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream()
                    .map(this::convertToDtoWithReplies)
                    .collect(Collectors.toList()));
        } else {
            dto.setReplies(Collections.emptyList());
        }
        return dto;
    }
}