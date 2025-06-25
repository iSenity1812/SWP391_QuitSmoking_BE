package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private User memberID; // Khóa ngoại tới bảng Member

    @Column(name = "ChallengeName", nullable = false, length = 100)
    private String challengeName;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "TargetValue", precision = 10, scale = 2)
    private BigDecimal targetValue;

    @Column(name = "Unit", length = 50)
    private String unit; // Ví dụ: 'cigarettes', 'VND'

    @Column(name = "Status", length = 50)
    private String status; // 'Active', 'Completed', 'Given Up'
}

