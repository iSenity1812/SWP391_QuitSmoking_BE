package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Qualification.QualificationId.class)
public class Qualification {
    // Composite key: CoachID + QualificationName
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", referencedColumnName = "coach_id", nullable = false)
    private Coach coach;

    @Id
    @Column(name = "qualification_name", nullable = false, length = 255)
    private String qualificationName;

    @Column(name = "issuing_organization", length = 255)
    private String issuingOrganization;

    @Column(name = "qualification_url", length = 512)
    private String qualificationURL;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @Column(name = "request_update_date", nullable = false)
    private LocalDateTime requestUpdateDate = LocalDateTime.now();

    @Column(name = "approve_by", length = 36)
    private String approveBy; // userId của admin duyệt (nullable)

    // Composite key class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QualificationId implements Serializable {
        private Coach coach;
        private String qualificationName;
    }
} 