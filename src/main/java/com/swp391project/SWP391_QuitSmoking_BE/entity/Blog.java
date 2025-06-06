package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "Blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BlogID", updatable = false, nullable = false)
    private Integer blogId;

    @NotNull(message = "AuthorID cannot be null")
    @Column(name = "AuthorID", nullable = false, columnDefinition = "uuid")
    private UUID authorId;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(name = "Title", length = 255, nullable = false)
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Column(name = "Content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "LastUpdated")
    private LocalDateTime lastUpdated;

    @Size(max = 20)
    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "ApprovedBy", columnDefinition = "uuid")
    private UUID approvedBy;

    @Column(name = "ApprovedAt")
    private LocalDateTime approvedAt;

    public UUID getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setBlogId(Integer blogId) {
        this.blogId = blogId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
}