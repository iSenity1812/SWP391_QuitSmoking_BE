package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChallengeID")
    private Integer challengeID;

    @Column(name = "MemberID")
    private UUID memberID; // Khóa ngoại tới bảng Member

    @Column(name = "ChallengeName", nullable = false, length = 100)
    private String challengeName;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "StartDate")
    private LocalDateTime startDate;

    @Column(name = "EndDate")
    private LocalDateTime endDate;

    @Column(name = "TargetValue", precision = 10, scale = 2)
    private BigDecimal targetValue;

    @Column(name = "Unit", length = 50)
    private String unit; // Ví dụ: 'cigarettes', 'USD', 'VND'

    @Column(name = "Status", length = 50)
    private String status; // 'Active', 'Completed', 'Given Up'
}

