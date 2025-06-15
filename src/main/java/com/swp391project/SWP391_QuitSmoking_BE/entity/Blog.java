// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/models/Blog.java

package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.Where;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blogs") // Đảm bảo tên bảng là "blogs"
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // int [pk, increment]
    @Column(name = "blog_id")
    private Integer blogId;

    @ManyToOne(fetch = FetchType.LAZY) // Many blogs to one user (author)
    @JoinColumn(name = "author_id", nullable = false) // AuthorID uuid [not null]
    private User author; // Giả định bạn có entity User

    @Column(name = "title", nullable = false, length = 255) // Title varchar(255) [not null]
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT") // Content text [not null]
    private String content;

    @CreationTimestamp // CreatedAt datetime [default: `now()`]
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // LastUpdated datetime
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING) // <-- RẤT QUAN TRỌNG: Lưu enum dưới dạng String trong DB
    private BlogStatus status; // Ví dụ: 'PENDING_APPROVAL', 'PUBLISHED', 'REJECTED'

    @ManyToOne(fetch = FetchType.LAZY) // Many blogs to one user (approved by)
    @JoinColumn(name = "approved_by", nullable = true) // ApprovedBy uuid (có thể null)
    private User approvedBy; // Giả định bạn có entity User

    @Column(name = "approved_at") // ApprovedAt datetime
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments; // Mối quan hệ một blog có nhiều comment

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false; // Mặc định là false (chưa xóa)
}