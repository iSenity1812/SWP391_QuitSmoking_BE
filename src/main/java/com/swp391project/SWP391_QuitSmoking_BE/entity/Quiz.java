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
@Table(name = "Quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "QuizID", updatable = false, nullable = false)
    private UUID quizId;

    @Column(name = "Title", nullable = false)
    @NotBlank(message = "Tiêu đề câu đố không được để trống")
    @Size(max = 255, message = "Tiêu đề câu đố không được vượt quá 255 ký tự")
    private String title;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

//    @Column(name = "ScorePossible")
//    @Min(value = 0, message = "Điểm tối đa không được là số âm")
//    private Integer scorePossible;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    @NotNull(message = "Ngày tạo không được để trống")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt", nullable = false)
    @NotNull(message = "Ngày cập nhật không được để trống")
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedByAdminID", referencedColumnName = "UserID", nullable = false)
    @NotNull(message = "Quản trị viên tạo câu đố không được để trống")
    // Đảm bảo CreatedByAdminID này là một User có Role.SUPER_ADMIN hoặc Role.CONTENT_ADMIN
    private User createdByAdmin;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Option> options = new HashSet<>();
}