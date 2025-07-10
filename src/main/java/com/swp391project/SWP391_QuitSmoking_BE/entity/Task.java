package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaskID", updatable = false, nullable = false)
    private Integer taskId;

    // TypeID: 1 cho Quiz Task, 2 cho Tip Task
    @Column(name = "TypeID", nullable = false)
    @NotNull(message = "Loại nhiệm vụ (TypeID) không được để trống")
    private Integer typeId;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    @NotNull(message = "Ngày tạo không được để trống")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Liên kết với người dùng đã kích hoạt Task này (tùy chọn)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", referencedColumnName = "UserID", nullable = true)
    private User createdByUser;

    // Mối quan hệ Many-to-Many với Quiz thông qua bảng TaskQuiz
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "TaskQuiz",
            joinColumns = @JoinColumn(name = "TaskID"),
            inverseJoinColumns = @JoinColumn(name = "QuizID")
    )
    private Set<Quiz> quizzes = new HashSet<>();

    // Mối quan hệ Many-to-Many với Tip thông qua bảng TaskTip
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "TaskTip",
            joinColumns = @JoinColumn(name = "TaskID"),
            inverseJoinColumns = @JoinColumn(name = "TipID")
    )
    private Set<Tip> tips = new HashSet<>();
}