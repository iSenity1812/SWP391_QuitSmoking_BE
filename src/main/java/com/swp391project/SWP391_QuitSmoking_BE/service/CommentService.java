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
        return commentRepository.save(comment);
    }

    public Comment updateComment(Integer id, Comment commentDetails) {
        return commentRepository.findById(id).map(comment -> {
            comment.setBlogId(commentDetails.getBlogId());
            comment.setUserId(commentDetails.getUserId());
            comment.setParentCommentId(commentDetails.getParentCommentId());
            comment.setContent(commentDetails.getContent());
            comment.setCommentDate(commentDetails.getCommentDate());
            return commentRepository.save(comment);
        }).orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }
}