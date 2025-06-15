package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "time_slot")
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeslot_id", updatable = false, nullable = false)
    private Integer timeSlotId;

    @Column(name = "label", nullable = false, length = 50)
    private String label;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // lưu giờ, phút

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // lưu giờ, phút

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false; // mặc định là false, không xóa

    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CoachSchedule> coachSchedules = new HashSet<>(); // TimeSlot có thể được dùng trong nhiều CoachSchedule
}
