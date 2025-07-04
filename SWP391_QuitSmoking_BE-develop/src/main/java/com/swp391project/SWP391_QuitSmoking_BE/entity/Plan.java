package com.swp391project.SWP391_QuitSmoking_BE.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "PlanID", updatable = false, nullable = false)
    private Integer planId;

    @NotBlank(message = "Tên gói không được để trống")
    @Size(max = 255, message = "Tên gói không được vượt quá 255 ký tự")
    @Column(name = "PlanName", length = 255, nullable = false, unique = true)
    private String planName;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Giá gói không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá gói phải không âm")
    @Digits(integer = 10, fraction = 2, message = "Giá gói có tối đa 10 chữ số phần nguyên và 2 chữ số phần thập phân")
    @Column(name = "Price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull(message = "Giá trị thời hạn không được để trống")
    @Min(value = 1, message = "Giá trị thời hạn phải lớn hơn 0")
    @Column(name = "DurationValue", nullable = false)
    private Integer durationValue; // Thời gian thực hiện kế hoạch (tính bằng ngày) (14, 30, 90)

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // Mối quan hệ One-to-Many với Subscription
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Subscription> subscriptions;

    // Mối quan hệ One-to-Many với Transaction
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

}
