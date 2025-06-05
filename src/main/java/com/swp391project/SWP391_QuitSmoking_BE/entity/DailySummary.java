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

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidDailySummaryDates
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
    @Column(name = "SmokedCount", nullable = false)
    private int smokedCount = 0;

    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    @Column(name = "CravingsCount", nullable = false)
    private int cravingsCount = 0;

    @NotNull(message = "Ngày theo dõi không được để trống")
    @PastOrPresent(message = "Ngày theo dõi không thể theo dõi ở tương lai")
    @Column(name = "TrackDate", nullable = false)
    private LocalDate trackDate;

     @Enumerated(EnumType.STRING)
     @Column(name = "Mood", length = 20)
     private Mood mood;

    @Column(name = "Note", columnDefinition = "TEXT")
    private String note;

    @NotNull(message = "Số tiền tiết kiệm không được để trống")
    @DecimalMin(value = "0.00", inclusive = true, message = "Số tiền tiết kiệm không thể là số âm")
    @Digits(integer = 8, fraction = 2, message = "Số tiền tiết kiệm có tối đa 8 chữ số phần nguyên và 2 chữ số phần thập phân")
    @Column(name = "MoneySaved", precision = 10, scale = 2, nullable = false)
    private BigDecimal moneySaved;

    @NotNull(message = "Trạng thái hoàn thành kế hoạch không được để trống")
    @Column(name = "IsPlanCompleted", nullable = false)
    private boolean isPlanCompleted = false;
}
