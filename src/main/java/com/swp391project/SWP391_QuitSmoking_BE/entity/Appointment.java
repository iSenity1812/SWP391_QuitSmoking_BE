package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AppointmentID", updatable = false, nullable = false)
    private Integer appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MemberID", referencedColumnName = "MemberID", nullable = false)
    @NotNull(message = "Thành viên không được để trống")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", referencedColumnName = "UserID", nullable = false)
    @NotNull(message = "Huấn luyện viên không được để trống")
    private Coach coach;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Column(name = "StartTime", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Column(name = "EndTime", nullable = false)
    private LocalDateTime endTime;

    @Size(max = 20)
    @Column(name = "Status", length = 20)
    private String status;

    @Size(max = 255)
    @Column(name = "Note", length = 255)
    private String note;

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public Member getMember() {
        return member;
    }

    public Coach getCoach() {
        return coach;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }
}
