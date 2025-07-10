package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Tip")
public class Tip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TipID", updatable = false, nullable = false)
    private UUID tipId;

    @Column(name = "Content", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Nội dung mẹo không được để trống")
    private String content;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    @NotNull(message = "Ngày tạo không được để trống")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedByUserID", referencedColumnName = "UserID", nullable = false)
    @NotNull(message = "Người dùng tạo mẹo không được để trống")
    // Đảm bảo CreatedByAdminID này là một User có Role.SUPER_ADMIN hoặc Role.CONTENT_ADMIN
    private User createdByUser;

    // Mối quan hệ Many-to-Many ngược với Task
    @ManyToMany(mappedBy = "tips")
    private Set<Task> tasks = new HashSet<>();
}