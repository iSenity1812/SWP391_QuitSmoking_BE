package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    private UUID memberId;

    private Integer subscriptionId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean subscriptionStatus = false;

    private Integer streak;

    @OneToOne
    @MapsId
    @JoinColumn(name = "memberId")
    private User user;
}
