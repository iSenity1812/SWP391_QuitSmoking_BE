package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointment")
// @NamedEntityGraph(
// name = "appointment.with.member.user.coachSchedule.coach.user.timeSlot",
// attributeNodes = {
// @NamedAttributeNode(value = "member", subgraph = "member-subgraph"),
// @NamedAttributeNode(value = "coachSchedule", subgraph =
// "coachSchedule-subgraph")
// },
// subgraphs = {
// @NamedSubgraph(
// name = "member-subgraph",
// attributeNodes = @NamedAttributeNode("user")
// ),
// @NamedSubgraph(
// name = "coachSchedule-subgraph",
// attributeNodes = {
// @NamedAttributeNode("timeSlot"),
// @NamedAttributeNode(value = "coach", subgraph = "coach-subgraph")
// }
// ),
// @NamedSubgraph(
// name = "coach-subgraph",
// attributeNodes = @NamedAttributeNode("user")
// )
// }
// )
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId; // Khóa chính của bảng Appointment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "MemberID", nullable = false)
    private Member member;

    // @OneToOne(fetch = FetchType.LAZY)
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

    // Additional getter/setter methods for compatibility
    public Member getMember() {
        return this.member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public CoachSchedule getCoachSchedule() {
        return this.coachSchedule;
    }

    public void setCoachSchedule(CoachSchedule coachSchedule) {
        this.coachSchedule = coachSchedule;
    }

    public AppointmentStatus getStatus() {
        return this.status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getBookingTime() {
        return this.bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
