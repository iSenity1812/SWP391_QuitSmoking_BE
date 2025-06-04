package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import com.swp391project.SWP391_QuitSmoking_BE.validation.quitplan.ValidQuitPlanDates;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidQuitPlanDates
public class QuitPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QuitPlanID", updatable = false, nullable = false)
    private Integer quitPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MemberID", referencedColumnName = "MemberID", nullable = false)
    @NotNull(message = "Thông tin thành viên không được để trống")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanTypeID", referencedColumnName = "PlanTypeID", nullable = false)
    @NotNull(message = "Loại kế hoạch không được để trống")
    private PlanType planType;

    @NotNull(message = "Loại kế hoạch giảm dần không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "ReductionType", length = 20, nullable = false)
    private ReductionQuitPlanType reductionType = ReductionQuitPlanType.LINEAR;

    @NotNull(message = "Ngày tạo kế hoạch không được để trống")
    @PastOrPresent(message = "Ngày tạo kế hoạch không thể ở tương lai")
    @Column(name = "CreatedAt", nullable =  false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull(message = "Ngày bắt đầu kế hoạch không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu kế hoạch không thể ở quá khứ")
    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate = LocalDateTime.now();

    @NotNull(message = "Ngày kết thúc mục tiêu không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc mục tiêu phải ở hiện tại hoặc tương lai")
    @Column(name = "GoalDate", nullable = false)
    private LocalDate goalDate;

    @Min(value = 0, message = "Số lượng thuốc ban đầu không thể là số âm")
    @Max(value = 200, message = "Số lượng thuốc ban đầu không thể vượt quá 200")
    @Column(name = "InitialSmokingAmount", nullable = false)
    private int initialSmokingAmount;

     @NotNull(message = "Trạng thái kế hoạch không được để trống")
     @Enumerated(EnumType.STRING)
     @Column(name = "Status", length = 20, nullable = false)
     private QuitPlanStatus status = QuitPlanStatus.IN_PROGRESS;
}
