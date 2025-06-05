package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID", updatable = false, nullable = false)
    private Integer roleId;

    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    @Column(name = "RoleName", length = 50, unique = true, nullable = false)
    private String roleName;
}