package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TransactionMethod")
public class TransactionMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionMethodID", updatable = false, nullable = false)
    private Integer transactionMethodId;

    @Column(name = "MethodName", nullable = false, length = 50, unique = true)
    private String methodName;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;
}
