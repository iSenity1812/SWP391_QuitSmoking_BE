package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId; // Khóa chính của bảng Appointment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "MemberID", nullable = false)
    private Member member;

    //    @OneToOne(fetch = FetchType.LAZY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private CoachSchedule coachSchedule; // Tham chiếu đến CoachSchedule đã được đặt

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note; // Ghi chú của thành viên về cuộc hẹn

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime; // Thời gian đặt lịch

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "agora_channel_name", unique = true)
    private String agoraChannelName;
}
