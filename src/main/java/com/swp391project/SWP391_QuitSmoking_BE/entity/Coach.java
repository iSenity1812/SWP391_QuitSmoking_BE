package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "coach")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coach {
    @Id
    private UUID coachId;

    private String fullName;

    @Column(columnDefinition = "text")
    private String coachBio;

    @OneToOne
    @MapsId
    @JoinColumn(name = "coachId")
    private User user;
}
