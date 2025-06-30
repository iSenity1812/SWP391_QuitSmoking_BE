package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.PlanType;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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
//    List<QuitPlan> findByPlanType(PlanType planType);

    /**
     * Tìm tất cả các kế hoạch cai thuốc với một loại giảm dần cụ thể.
     */
    List<QuitPlan> findByReductionType(ReductionQuitPlanType reductionType);

    QuitPlan findByMember_MemberIdAndQuitPlanId(UUID memberId,Integer quitPlanId);

    List<QuitPlan> findByMemberAndStatusIn(Member member, List<QuitPlanStatus> statuses);

    @Modifying
    @Query("UPDATE QuitPlan q SET q.status = :status WHERE q.quitPlanId = :quitPlanId")
    void updateQuitPlanStatus(Integer quitPlanId, QuitPlanStatus status);

    @Query("SELECT qp FROM QuitPlan qp JOIN FETCH qp.member m JOIN FETCH m.user u")
    List<QuitPlan> findAllWithMemberAndUser();

    @Query("SELECT q FROM QuitPlan q JOIN FETCH q.member m JOIN FETCH m.user WHERE q.quitPlanId = :quitPlanId")
    Optional<QuitPlan> findByIdWithMemberAndUser(Integer quitPlanId);

    @Query("SELECT q FROM QuitPlan q JOIN FETCH q.member m JOIN FETCH m.user WHERE m.memberId = :memberId ORDER BY q.createdAt DESC")
    List<QuitPlan> findByMemberIdWithMemberAndUser(@Param("memberId") UUID memberId);

    Optional<QuitPlan> findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(UUID memberId, QuitPlanStatus quitPlanStatus);

    // Method for achievement calculation
    Optional<QuitPlan> findByMember_MemberIdOrderByCreatedAtDesc(UUID memberId);
}
