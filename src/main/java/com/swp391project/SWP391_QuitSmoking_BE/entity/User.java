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
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @NotBlank(message = "Tên người dùng không được để trống") // Không được null và không được rỗng/chỉ chứa khoảng trắng
    @Size(min = 3, max = 50, message = "Tên người dùng phải có từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Tên người dùng chỉ được chứa chữ cái, số và dấu gạch dưới")
    @Column(name = "Username", length = 50, unique = true, nullable = false)
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
    @PastOrPresent(message = "Thời gian tạo không thể ở tương lai") //LocalDateTime.now() luôn trả về thời gian hiện tại hoặc trong quá khứ
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false)
    private Role role;

    @NotNull(message = "Trạng thái hoạt động không được để trống")
    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true; //đặt giá trị mặc định

    @Size(max = 255, message = "Đường dẫn ảnh đại diện không được vượt quá 255 ký tự")
    @Pattern(
            regexp = "^[a-zA-Z0-9_\\-./]+\\.(jpg|jpeg|png|gif|webp|bmp|svg)$",
            message = "Tên file ảnh không hợp lệ hoặc định dạng không được hỗ trợ"
    )
    @Column(name = "ProfilePicture", length = 255)
    private String profilePicture;

    @JdbcTypeCode(SqlTypes.JSON) // Annotation của Hibernate để xử lý JSON
    @Column(name = "NotificationSetting", columnDefinition = "json")
    @NotNull(message = "Cài đặt thông báo không được để trống")
    private Map<String, Object> notificationSetting;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
