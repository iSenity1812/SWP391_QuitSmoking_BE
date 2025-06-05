package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import com.swp391project.SWP391_QuitSmoking_BE.validation.cravingtracking.ValidCravingTrackingData;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidCravingTrackingData
//Mỗi TrackTime phải duy nhất cho mỗi DailySummary
//Mỗi giờ chỉ có duy nhất 1 record có thể lưu
 @Table(name = "CravingTrackings", uniqueConstraints = {
     @UniqueConstraint(columnNames = {"DailySummaryID", "TrackTime"})
 })
public class CravingTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CravingTrackingID", updatable = false, nullable = false)
    private Integer cravingTrackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DailySummaryID", referencedColumnName = "DailySummaryID", nullable = false)
    @NotNull(message = "Theo dõi cơn thèm phải thuộc về một nhật ký hàng ngày")
    private DailySummary dailySummary;

    @NotNull(message = "Thời gian theo dõi không được để trống")
    @PastOrPresent(message = "Thời gian theo dõi không thể ở tương lai")
    @Column(name = "TrackTime", nullable = false)
    private LocalDateTime trackTime;

    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    @Column(name = "SmokedCount", nullable = false)
    private Integer smokedCount = 0;

    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    @Column(name = "CravingsCount", nullable = false)
    private Integer cravingsCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "Situation", length = 20)
    private Situation situation;

    @Enumerated(EnumType.STRING)
    @Column(name = "WithWhom", length = 50)
    private WithWhom withWhom;
}
