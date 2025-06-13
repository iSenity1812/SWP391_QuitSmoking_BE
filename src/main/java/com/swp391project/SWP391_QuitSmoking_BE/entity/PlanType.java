//package com.swp391project.SWP391_QuitSmoking_BE.entity;
//
//import com.swp391project.SWP391_QuitSmoking_BE.enums.DurationType;
//import com.swp391project.SWP391_QuitSmoking_BE.interfaces.IDurationAware;
//import com.swp391project.SWP391_QuitSmoking_BE.validation.ValidDurationTypeConstraint;
//import jakarta.persistence.Entity;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@ValidDurationTypeConstraint
//public class PlanType {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "PlanTypeID", updatable = false, nullable = false, columnDefinition = "INTEGER USING plan_typeid::integer")
//    private Integer planTypeID;
//
//    @NotBlank(message = "Tên loại kế hoạch không được để trống")
//    @Size(max = 50, message = "Tên loại kế hoạch không được vượt quá 50 ký tự")
//    @Column(name = "PlanName", length = 50, unique = true, nullable = false)
//    private String planName;
//
//    @Column(name = "Description", columnDefinition = "TEXT")
//    private String description;
//
//    @NotNull(message = "Loại thời gian không được để trống")
//    private boolean isGradualReduction;
//
//
//}
