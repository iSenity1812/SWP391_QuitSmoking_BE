package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import com.swp391project.SWP391_QuitSmoking_BE.validation.quitplan.ValidQuitPlanDates;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

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
    // @FutureOrPresent(message = "Ngày tạo kế hoạch phải ở hiện tại hoặc tương
    // lai") // Bỏ comment nếu muốn validation ngày tạo là hiện tại/tương lai
    @Column(name = "CreateDate", nullable = false)
    private LocalDate createDate;

    @NotNull(message = "Ngày bắt đầu mục tiêu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu mục tiêu phải ở hiện tại hoặc tương lai")
    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc mục tiêu không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc mục tiêu phải ở hiện tại hoặc tương lai")
    @Column(name = "GoalDate", nullable = false)
    private LocalDate goalDate;

    @Min(value = 0, message = "Số lượng thuốc ban đầu không thể là số âm")
    @Max(value = 500, message = "Số lượng thuốc ban đầu không thể vượt quá 500")
    @Column(name = "InitialSmokingAmount", nullable = false)
    private int initialSmokingAmount;

    @NotNull(message = "Số điều thuốc trong mỗi gói không được để trống")
    @Min(value = 1, message = "Số điều thuốc trong mỗi gói phải lớn hơn 0")
    @Max(value = 50, message = "Số điều thuốc trong mỗi gói không thể vượt quá 50")
    @Column(name = "CigarettesPerPack", nullable = false)
    @PositiveOrZero(message = "Số điều thuốc trong mỗi gói phải là số dương hoặc bằng 0")
    private int cigarettesPerPack;

    // THÊM TRƯỜNG NÀY ĐỂ ĐỒNG BỘ VỚI LOGIC CỦA ACHIEVEMENT
    @Column(name = "InitialCigarettesPerDay")
    private Integer initialCigarettesPerDay;

    @NotNull(message = "Số tiền/gói thuốc không được để trống")
    @DecimalMin(value = "0.00", inclusive = true, message = "Số tiền không thể là số âm")
    // 6 số nguyên, 2 số thập phân
    @DecimalMax(value = "999999.99", inclusive = true, message = "Số tiền chỉ có thể tối đa 999.999VND")
    @Column(name = "PricePerPack", precision = 8, scale = 2, nullable = false)
    private BigDecimal pricePerPack;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20, nullable = false)
    private QuitPlanStatus status = QuitPlanStatus.NOT_STARTED;

    @OneToMany(mappedBy = "quitPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailySummary> dailySummaries;

    // Relationship with Member (bi-directional with Member)
    // Removed @OneToOne and @MapsId based on provided structure.
    // Assuming Member is managed separately, and QuitPlan only references Member by
    // MemberID.
    // If you need direct access to User from QuitPlan via Member, ensure
    // FetchType.LAZY is handled.
}