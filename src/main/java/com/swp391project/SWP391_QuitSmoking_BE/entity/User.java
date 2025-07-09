package com.swp391project.SWP391_QuitSmoking_BE.entity;


import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UserID", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID userId;

    // Mối quan hệ One-to-One với Member (bên không sở hữu)
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Member member; // Tham chiếu đến đối tượng Member liên kết

    // Mối quan hệ One-to-One với Coach (bên không sở hữu)
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Coach coach; // Tham chiếu đến đối tượng Coach liên kết

    // Không được null và không được rỗng/chỉ chứa khoảng trắng
    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải có từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Tên người dùng chỉ được chứa chữ cái, số và dấu gạch dưới")
    @Column(name = "Username", length = 50, nullable = false)
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    @Column(name = "Email", length = 100, unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @NotNull(message = "Thời gian tạo không được để trống")
    @PastOrPresent(message = "Thời gian tạo không thể ở tương lai")
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PastOrPresent(message = "Thời gian cập nhật không thể ở tương lai")
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false)
    private Role role;

    @NotNull(message = "Trạng thái hoạt động không được để trống")
    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true; //đặt giá trị mặc định

    @Size(max = 255, message = "Đường dẫn ảnh đại diện không được vượt quá 255 ký tự")
    @Pattern(
            regexp = "^$|.*\\.(jpg|jpeg|png|gif|webp|bmp|svg)$",
            message = "Tên file ảnh không hợp lệ hoặc định dạng không được hỗ trợ",
            flags = Pattern.Flag.CASE_INSENSITIVE
    )
    @Column(name = "ProfilePicture", length = 255)
    private String profilePicture;

    @JdbcTypeCode(SqlTypes.JSON) // Annotation của Hibernate để xử lý JSON
    @Column(name = "NotificationSetting", columnDefinition = "jsonb")
    @NotNull(message = "Cài đặt thông báo không được để trống")
    private Map<String, Object> notificationSetting;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Subscription> subscriptions;

    // Mối quan hệ One-to-Many với Transaction
    // User sở hữu nhiều Transaction
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Transaction> transactions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (this.role == null) {
            return List.of(); // Trả về rỗng nếu role null (không mong muốn)
        }
        // Ví dụ: Role.SUPER_ADMIN.name() sẽ là "SUPER_ADMIN", sau đó thành "ROLE_SUPER_ADMIN"
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }


    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
