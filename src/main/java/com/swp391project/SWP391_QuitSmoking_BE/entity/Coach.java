package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.CoachSpecialty;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coach {
    //CoachID dùng chung UserID làm khóa chính
    //là khóa ngoại liên kết với bảng User
    @Id
    @Column(name = "coach_id")
    private UUID coachId;

    @OneToOne(fetch = FetchType.LAZY) //mối quan hệ với entity User
    @MapsId //chỉ định khóa chính của Coach được lấy từ khóa chính của User
    @JoinColumn(name = "coach_id")
    @NotNull(message = "Người dùng liên kết không được để trống")
    private User user; // Tham chiếu đến đối tượng User mà Coach này thuộc về

    @Column(precision = 3, scale = 2) // DECIMAL(3, 2)
    private BigDecimal rating;

    @NotBlank(message = "Tên đầy đủ của huấn luyện viên không được để trống")
    @Size(max = 255, message = "Tên đầy đủ không được vượt quá 255 ký tự")
    @Column(name = "FullName", length = 255, nullable = false)
    private String fullName;

    @Column(name = "CoachBio", columnDefinition = "TEXT")
    private String coachBio;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<CoachSchedule> coachSchedules = new HashSet<>(); // Danh sách lịch làm việc của huấn luyện viên

    @ElementCollection(targetClass = CoachSpecialty.class) // Cho biết đây là một collection của các enum
    @CollectionTable(name = "coach_specialties", // Tên bảng sẽ được tạo để lưu các chuyên môn
            joinColumns = @JoinColumn(name = "coach_id")) // Khóa ngoại liên kết với bảng Coach
    @Enumerated(EnumType.STRING) // Lưu trữ tên của enum (ví dụ: "BEHAVIORAL_THERAPY") thay vì số thứ tự
    @Column(name = "specialty") // Tên cột trong bảng coach_specialties sẽ lưu giá trị enum
    private Set<CoachSpecialty> specialties = new HashSet<>();
}
