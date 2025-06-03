package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.DurationType;
import com.swp391project.SWP391_QuitSmoking_BE.validation.ValidDurationTypeConstraint;
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
@ValidDurationTypeConstraint
public class PlanType {
    @Id
    @Column(name = "PlanTypeID", length = 50, updatable = false, nullable = false)
    @NotBlank(message = "ID loại kế hoạch không được để trống")
    @Size(max = 50, message = "ID loại kế hoạch không được vượt quá 50 ký tự")
    private String planTypeID;

    @NotBlank(message = "Tên loại kế hoạch không được để trống")
    @Size(max = 50, message = "Tên loại kế hoạch không được vượt quá 50 ký tự")
    @Column(name = "PlanName", length = 50, unique = true, nullable = false)
    private String planName;

    @NotNull(message = "Thời lượng kế hoạch không được để trống")
    @Column(name = "Duration", nullable = false)
    private Integer duration;

    @NotNull(message = "Dạng thời lượng kế hoạch không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "DurationType", nullable = false)
    private DurationType durationType;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;
}
