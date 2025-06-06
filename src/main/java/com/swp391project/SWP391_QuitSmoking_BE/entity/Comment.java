package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentID", updatable = false, nullable = false)
    private Integer commentId;

    @NotNull(message = "BlogID cannot be null")
    @Column(name = "BlogID", nullable = false)
    private Integer blogId;

    @NotNull(message = "UserID cannot be null")
    @Column(name = "UserID", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "ParentCommentID")
    private Integer parentCommentId;

    @NotBlank(message = "Content cannot be blank")
    @Column(name = "Content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "CommentDate", nullable = false)
    private LocalDateTime commentDate = LocalDateTime.now();

    public Integer getBlogId() {
        return blogId;
    }

    public java.util.UUID getUserId() {
        return userId;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }

    public String getContent() {
        return content;
    }

    public java.time.LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public void setBlogId(Integer blogId) {
        this.blogId = blogId;
    }

    public void setUserId(java.util.UUID userId) {
        this.userId = userId;
    }

    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCommentDate(java.time.LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }
}