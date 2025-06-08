package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.validation.subscription.ValidSubscriptionDuration;
import com.swp391project.SWP391_QuitSmoking_BE.validation.subscription.ValidSubscriptionState;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidSubscriptionDuration  //Custom annotation cho thời lượng đăng kí
@ValidSubscriptionState     //Custom annotation kiểm tra tính nhất quán về trạng thái đăng kí
public class Member {
    //MemberID dùng chung UserID làm khóa chính
    //là khóa ngoại liên kết với bảng User
    @Id
    @Column(name = "MemberID")
    private UUID memberId;

    @OneToOne //mối quan hệ với entity User
    @MapsId //Khóa chính của Member được lấy từ khóa chính của User
    @JoinColumn(name = "MemberID", referencedColumnName = "UserID")
    @NotNull(message = "Người dùng liên kết không được để trống")
    private User user; //Tham chiếu đến đối tượng User mà Member này thuộc về

    @Min(value = 0, message = "Streak không thể là số âm")
    @Column(name = "Streak", nullable = false)
    private int streak = 0;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSubscription> memberSubscriptions; // Danh sách các gói đăng ký của thành viên

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuitPlan> quitPlans; // Danh sách các kế hoạch bỏ thuốc lá của thành viên
}
