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
import java.util.UUID; // Import UUID

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Option")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // OptionID là Integer, tự động tăng
    @Column(name = "OptionID", updatable = false, nullable = false)
    private Integer optionId; // OptionID là Integer

    // Quan hệ Many-to-One với Quiz, QuizID bây giờ là UUID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QuizID", referencedColumnName = "QuizID", nullable = false) // Tham chiếu tới UUID QuizID
    @NotNull(message = "Quiz không được để trống cho một lựa chọn")
    private Quiz quiz; // QuizID là UUID

    @Column(name = "Content", nullable = false)
    @NotBlank(message = "Nội dung lựa chọn không được để trống")
    @Size(max = 255, message = "Nội dung lựa chọn không được vượt quá 255 ký tự")
    private String content;

    @Column(name = "IsCorrect", nullable = false)
    private Boolean isCorrect = false;

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
    @NotNull(message = "Quản trị viên tạo lựa chọn không được để trống")
    // Đảm bảo CreatedByAdminID này là một User có Role.SUPER_ADMIN hoặc Role.CONTENT_ADMIN
    private User createdByAdmin; // UserID là UUID
}