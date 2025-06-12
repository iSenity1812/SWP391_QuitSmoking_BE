package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.PlanType;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuitPlanRepository extends JpaRepository<QuitPlan, Integer> {
    // Tìm kiếm QuitPlan theo memberId
    List<QuitPlan> findByMember_MemberId(UUID memberId);

    // Kiểm tra xem QuitPlan có tồn tại với memberId và status là ACTIVE không
    boolean existsByMember_MemberIdAndStatus(UUID memberId, QuitPlanStatus status); // Hoặc QuitPlanStatus enum

    // Tìm tất cả các kế hoạch cai thuốc của một thành viên
    List<QuitPlan> findByMemberOrderByCreatedAtDesc(Member member);

    /**
     * Tìm tất cả các kế hoạch cai thuốc với trạng thái cụ thể.
     */
    List<QuitPlan> findByStatus(QuitPlanStatus status);

    /**
     * Tìm tất cả các kế hoạch cai thuốc dựa trên loại kế hoạch (PlanType).
     * Truy vấn thông qua đối tượng PlanType được liên kết.
     */
    List<QuitPlan> findByPlanType(PlanType planType);

    /**
     * Tìm tất cả các kế hoạch cai thuốc với một loại giảm dần cụ thể.
     */
    List<QuitPlan> findByReductionType(ReductionQuitPlanType reductionType);

    QuitPlan findByMember_MemberIdAndQuitPlanId(UUID memberId,Integer quitPlanId);
}
