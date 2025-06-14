// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/models/Comment.java

package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments") // Đảm bảo tên bảng là "comments"
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // int [pk, increment]
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne(fetch = FetchType.LAZY) // Many comments to one blog
    @JoinColumn(name = "blog_id", nullable = false) // BlogID int [not null]
    private Blog blog; // Mối quan hệ với Blog entity

    @ManyToOne(fetch = FetchType.LAZY) // Many comments to one user
    @JoinColumn(name = "user_id", nullable = false) // UserID uuid [not null]
    private User user; // Giả định bạn có entity User

    @ManyToOne(fetch = FetchType.LAZY) // Self-referencing for replies
    @JoinColumn(name = "parent_comment_id", nullable = true) // ParentCommentID int
    private Comment parentComment; // Để hỗ trợ bình luận lồng nhau

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("commentDate ASC") // Sắp xếp các câu trả lời theo thời gian
    private List<Comment> replies; // Danh sách các câu trả lời cho bình luận này

    @Column(name = "content", nullable = false, columnDefinition = "TEXT") // Content text [not null]
    private String content;

    @CreationTimestamp // CommentDate datetime [default: `now()`]
    @Column(name = "comment_date", nullable = false, updatable = false)
    private LocalDateTime commentDate;

    // Constructors, getters, setters được tạo bởi Lombok
}