package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuitPlanRepository extends JpaRepository<QuitPlan, Integer>  {
    // Phương thức để tìm kiếm các QuitPlan đang status theo Member
    List<QuitPlan> findByMemberAndStatus(Member member, QuitPlanStatus status);

    // Phương thức để tìm kiếm các QuitPlan đang status theo ID của Member
    List<QuitPlan> findByMember_MemberIdAndStatus(UUID memberId, QuitPlanStatus status);

    // tối đa một kế hoạch in_progress cho mỗi thành viên -> dùng optional
    Optional<QuitPlan> findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(UUID memberId, QuitPlanStatus status);

}
