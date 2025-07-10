// src/main/java/com/swp391project.SWP391_QuitSmoking_BE/entity/Blog.java

package com.swp391project.SWP391_QuitSmoking_BE.entity; // Gói của bạn là .entity chứ không phải .models như comment

import com.swp391project.SWP391_QuitSmoking_BE.enums.BlogStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where; // <--- Đảm bảo import này
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blogs")
@Where(clause = "is_deleted = false") // <--- THÊM DÒNG NÀY VÀO ĐÂY
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Integer blogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    private BlogStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = true)
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    // Để lọc comments đã xóa mềm, Comment entity cũng cần @Where(clause = "is_deleted = false")
    // và/hoặc bạn có thể thêm @Where(clause = "is_deleted = false") ngay trên mối quan hệ này
    // Tuy nhiên, nếu Comment entity có @Where thì không cần thêm ở đây nữa.
    private List<Comment> comments;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
