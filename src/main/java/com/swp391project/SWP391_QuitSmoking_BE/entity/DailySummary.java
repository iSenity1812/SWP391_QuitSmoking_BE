package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Mood;
import com.swp391project.SWP391_QuitSmoking_BE.validation.dailysummary.ValidDailySummaryDates;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ValidDailySummaryDates
@Table(uniqueConstraints = { //Ràng buộc duy nhất trên cặp cột
        @UniqueConstraint(columnNames = {"QuitPlanID", "TrackDate"})
})
public class DailySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DailySummaryID", updatable = false, nullable = false)
    private Integer dailySummaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QuitPlanID", referencedColumnName = "QuitPlanID", nullable = false)
    @NotNull(message = "Kế hoạch cai thuốc không được để trống trong nhật ký hàng ngày")
    private QuitPlan quitPlan;

    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    @Column(name = "TotalSmokedCount")
    private int totalSmokedCount = 0; // Tổng số điếu đã hút (tính từ CT + thủ công)

    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    @Column(name = "TotalCravingCount")
    private int totalCravingCount = 0; // Tổng số lần thèm thuốc (tính từ CT + thủ công)

    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    @Column(name = "ManualSmokedCount")
    private int manualSmokedCount = 0; // Số điếu hút nhập thủ công

    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    @Column(name = "ManualCravingCount")
    private int manualCravingCount = 0; // Số lần thèm thuốc nhập thủ công

    @Min(value = 0, message = "Số lượng thuốc đã hút từ theo dõi cơn thèm không thể là số âm")
    @Column(name = "TrackedSmokedCount")
    private int trackedSmokedCount = 0; // Số điếu hút được tổng hợp từ CravingTracking

    @Min(value = 0, message = "Số lần thèm thuốc từ theo dõi cơn thèm không thể là số âm")
    @Column(name = "TrackedCravingCount")
    private int trackedCravingCount = 0; // Số lần thèm thuốc được tổng hợp từ CravingTracking

    @NotNull(message = "Ngày theo dõi không được để trống")
    @PastOrPresent(message = "Ngày theo dõi không thể theo dõi ở tương lai")
    @Column(name = "TrackDate", nullable = false)
    private LocalDate trackDate;

     @Enumerated(EnumType.STRING)
     @Column(name = "Mood", length = 20)
     private Mood mood;

    @Column(name = "Note", columnDefinition = "TEXT")
    private String note;

//    @NotNull(message = "Số tiền tiết kiệm không được để trống")
//    @DecimalMin(value = "0.00", inclusive = true, message = "Số tiền không thể là số âm")
    //6 số nguyên, 2 số thập phân
//    @DecimalMax(value = "999999.99", inclusive = true, message = "Số tiền chỉ có thể tối đa 999.999VND")
    @Column(name = "MoneySaved")
    private BigDecimal moneySaved;

    @Column(name = "IsGoalAchieved", nullable = false)
    private boolean isGoalAchievedToday = false; // Trạng thái hoàn thành mục tiêu của ngày hôm nay

//    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

//    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dailySummary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CravingTracking> cravingTrackings = new ArrayList<>();; // Danh sách các theo dõi cơn thèm trong ngày này
}
