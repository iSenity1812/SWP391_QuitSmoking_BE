package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "UserID")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY) //mối quan hệ với entity User
    @MapsId //chỉ định khóa chính của Coach được lấy từ khóa chính của User
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    @NotNull(message = "Người dùng liên kết không được để trống")
    private User user; // Tham chiếu đến đối tượng User mà Coach này thuộc về

    @NotBlank(message = "Tên đầy đủ của huấn luyện viên không được để trống")
    @Size(max = 255, message = "Tên đầy đủ không được vượt quá 255 ký tự")
    @Column(name = "FullName", length = 255, nullable = false)
    private String fullName;

    @Column(name = "CoachBio", columnDefinition = "TEXT")
    private String coachBio;
}
