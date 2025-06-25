package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import com.swp391project.SWP391_QuitSmoking_BE.validation.quitplan.ValidQuitPlanDates;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidQuitPlanDates
@ToString
public class QuitPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QuitPlanID", updatable = false, nullable = false)
    private Integer quitPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MemberID", referencedColumnName = "MemberID", nullable = false)
    @NotNull(message = "Thông tin thành viên không được để trống")
    private Member member;

    @NotNull(message = "Loại kế hoạch giảm dần không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "ReductionType", length = 20, nullable = false)
    private ReductionQuitPlanType reductionType;

    @NotNull(message = "Ngày tạo kế hoạch không được để trống")
//    @PastOrPresent(message = "Ngày tạo kế hoạch không thể ở tương lai")
    @Column(name = "CreatedAt", nullable =  false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

//    @NotNull(message = "Ngày bắt đầu kế hoạch không được để trống")
//    @FutureOrPresent(message = "Ngày bắt đầu kế hoạch không thể ở quá khứ")
    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

//    @FutureOrPresent(message = "Ngày kết thúc mục tiêu phải ở hiện tại hoặc tương lai")
    @NotNull(message = "Ngày kết thúc mục tiêu không được để trống")
    @Column(name = "GoalDate", nullable = false)
    private LocalDate goalDate;

    @NotNull(message = "Số lượng thuốc ban đầu không được để trống")
    @Min(value = 0, message = "Số lượng thuốc ban đầu không thể là số âm")
    @Max(value = 500, message = "Số lượng thuốc ban đầu không thể vượt quá 500")
    @Column(name = "InitialSmokingAmount", nullable = false)
    private int initialSmokingAmount;

    @Min(value = 1, message = "Số điều thuốc trong mỗi gói phải lớn hơn 0")
    @Max(value = 50, message = "Số điều thuốc trong mỗi gói không thể vượt quá 50")
    @Column(name = "CigarettesPerPack")
    @PositiveOrZero(message = "Số điều thuốc trong mỗi gói phải là số dương hoặc bằng 0")
    private int cigarettesPerPack;

    @DecimalMin(value = "0.00", inclusive = true, message = "Số tiền không thể là số âm")
    //6 số nguyên, 2 số thập phân
    @DecimalMax(value = "999999.99", inclusive = true, message = "Số tiền chỉ có thể tối đa 999.999VND")
    @Column(name = "PricePerPack", precision = 8, scale = 2)
    private BigDecimal PricePerPack;

    @NotNull(message = "Trạng thái kế hoạch không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20, nullable = false)
    private QuitPlanStatus status;
}