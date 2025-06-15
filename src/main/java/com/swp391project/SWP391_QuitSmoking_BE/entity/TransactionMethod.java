package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    @Column(name = "TransactionMethodID", updatable = false, nullable = false)
    private Integer transactionMethodId;

    @NotBlank(message = "Tên phương thức giao dịch không được để trống")
    @Size(max = 100, message = "Tên phương thức giao dịch không được vượt quá 100 ký tự")
    @Column(name = "MethodName", length = 100, unique = true, nullable = false)
    private String methodName;

    @NotNull(message = "Trạng thái hoạt động không được để trống")
    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;
}
