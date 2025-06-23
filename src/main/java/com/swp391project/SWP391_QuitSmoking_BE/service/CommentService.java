package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Comment;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Optional<Comment> getCommentById(Integer id) {
        return commentRepository.findById(id);
    }

    public Comment createComment(Comment comment) {
        // Có thể thêm logic nghiệp vụ trước khi lưu
        return commentRepository.save(comment);
    }

    public Comment updateComment(Integer id, Comment commentDetails) {
        return commentRepository.findById(id).map(comment -> {
            // Cập nhật các trường của comment từ commentDetails
            comment.setContent(commentDetails.getContent());
            // Ví dụ: comment.setBlog(commentDetails.getBlog());
            // Ví dụ: comment.setUser(commentDetails.getUser());
            // ... thêm các trường khác bạn muốn cập nhật

            return commentRepository.save(comment);
        }).orElseThrow(() -> new RuntimeException("Comment not found with id " + id)); // Hoặc sử dụng exception tùy chỉnh của bạn
    }

    public void deleteComment(Integer id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with id " + id); // Hoặc sử dụng exception tùy chỉnh
        }
        commentRepository.deleteById(id);
    }
}