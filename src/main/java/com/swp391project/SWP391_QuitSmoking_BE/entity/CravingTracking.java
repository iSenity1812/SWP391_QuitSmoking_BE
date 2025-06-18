package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
//import com.swp391project.SWP391_QuitSmoking_BE.validation.cravingtracking.ValidCravingTrackingData;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ValidCravingTrackingData
//Mỗi TrackTime phải duy nhất cho mỗi DailySummary
//Mỗi giờ chỉ có duy nhất 1 record có thể lưu
@Table(name = "CravingTrackings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"DailySummaryID", "TrackTime"}) // Đảm bảo unique theo DailySummary và TrackTime
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
    private LocalDateTime trackTime; // lưu chính xác thời gian theo dõi cơn thèm thuốc

    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    @Column(name = "SmokedCount")
    private int smokedCount = 0;

    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    @Column(name = "CravingsCount")
    private int cravingsCount = 0;

    @JdbcTypeCode(SqlTypes.JSON) // Lưu dưới dạng JSON array của các chuỗi enum
    @Column(name = "Situations", columnDefinition = "jsonb")
    private Set<Situation> situations;

    @JdbcTypeCode(SqlTypes.JSON) // Lưu dưới dạng JSON array của các chuỗi enum
    @Column(name = "WithWhoms", columnDefinition = "jsonb")
    private Set<WithWhom> withWhoms;
}
