package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "coach_schedule", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"coach_id", "timeslot_id", "schedule_date"}) // Unique Constraint ở đây
})
public class CoachSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", updatable = false, nullable = false)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id", nullable = false)
    private TimeSlot timeSlot; // Tham chiếu đến TimeSlot đã được chọn

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate; // Chỉ lưu ngày, tháng, năm

    @Column(name = "is_booked", nullable = false)
    private boolean isBooked = false; // Default to false

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

//    @OneToOne(mappedBy = "coachSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    private Appointment appointment; // Tham chiếu đến đối tượng Appointment liên kết

    @OneToMany(mappedBy = "coachSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>(); // Sử dụng List để chứa nhiều Appointments

}
