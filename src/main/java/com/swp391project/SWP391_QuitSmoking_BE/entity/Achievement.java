package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AchievementID", updatable = false, nullable = false)
    private Integer achievementId;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(name = "Name", length = 100, nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "IconUrl", length = 255)
    private String iconUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "Criteria", columnDefinition = "json")
    private String criteria;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}