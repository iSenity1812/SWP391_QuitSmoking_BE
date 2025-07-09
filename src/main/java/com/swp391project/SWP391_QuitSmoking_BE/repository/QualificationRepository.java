package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Qualification;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Qualification.QualificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, QualificationId> {
    // Lấy tất cả qualification của 1 coach
    List<Qualification> findByCoach_CoachId(java.util.UUID coachId);
} 