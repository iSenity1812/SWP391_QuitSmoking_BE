package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.DurationType;
import com.swp391project.SWP391_QuitSmoking_BE.interfaces.IDurationAware;
import com.swp391project.SWP391_QuitSmoking_BE.validation.ValidDurationTypeConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidDurationTypeConstraint //custom annotation kiểm tra giá trị giữa durationType và duration
public class Subscription implements IDurationAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    @Column(name = "SubscriptionID", updatable = false, nullable = false)
    private Integer subscriptionId;

    @NotBlank(message = "Tên gói đăng ký không được để trống")
    @Size(max = 100, message = "Tên gói đăng ký không được vượt quá 100 ký tự")
    @Column(name = "Name", length = 100, unique = true, nullable = false)
    private String name;

    @NotNull(message = "Giá gói đăng ký không được để trống")
    // Giá phải >= 0 (inclusive = true: cho phép = 0, false thì phải > 0)
    @DecimalMin(value = "0.00", inclusive = true, message = "Giá gói đăng ký không thể là số âm")
    @Digits(integer = 6, fraction = 2, message = "Giá gói đăng ký phải có tối đa 6 chữ số phần nguyên và 2 chữ số phần thập phân")
    @Column(name = "Price", precision = 8, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull(message = "Thời lượng gói đăng ký không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn hoặc bằng 1")
    @Column(name = "Duration", nullable = false)
    private Integer duration; //Chỉ lưu giá trị thời gian

    @NotNull(message = "Dạng thời lượng gói đăng ký không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "DurationType", nullable = false)
    private DurationType durationType; //Lưu dạng thời lượng

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;
}
