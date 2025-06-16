package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class QuitPlanService {
    private final QuitPlanRepository quitPlanRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public QuitPlanService(
            QuitPlanRepository quitPlanRepository,
            MemberRepository memberRepository
    ) {
        this.quitPlanRepository = quitPlanRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Optional<QuitPlan> getProgressQuitPlansByMemberId(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với ID: " + memberId));
        return quitPlanRepository.findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(memberId, QuitPlanStatus.IN_PROGRESS);
    }

//    //Method để chuyển đổi Entity sang Response
//    private QuitPlanResponse convertToResponseDto(QuitPlan quitPlan) {
//        QuitPlanResponse response = new QuitPlanResponse();
//        response.setPlanTypeName(quitPlan.getPlanType().getPlanName());
//        response.setReductionType(quitPlan.getReductionType());
//        response.setStartDate(quitPlan.getStartDate());
//        response.setGoalDate(quitPlan.getGoalDate());
//        response.setInitialSmokingAmount(quitPlan.getInitialSmokingAmount());
//        response.setStatus(quitPlan.getStatus());
//
//        return response;
//    }
//
//    @Transactional
//    public QuitPlanResponse createQuitPlan(QuitPlanRequest request) {
//        // Kiểm tra sự tồn tại của Member (ID của Member chính là ID của User)
//        // Vì Member entity sử dụng @MapsId, memberRepository.findById(userId) sẽ tìm Member có userId đó.
//        Member member = memberRepository.findById(request.getMemberId())
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + request.getMemberId()));
//
//        // Kiểm tra sự tồn tại của PlanType
//        PlanType planType = planTypeRepository.findById(request.getPlanTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại kế hoạch với ID: " + request.getMemberId()));
//
//        QuitPlan quitPlan = new QuitPlan();
//        quitPlan.setMember(member);
//        quitPlan.setPlanType(planType);
//        quitPlan.setReductionType(request.getReductionType());
//        quitPlan.setCreatedAt(LocalDateTime.now()); // Đặt ngày tạo là hiện tại
//
//        LocalDate requestStartDate = request.getStartDate().toLocalDate();
//        LocalDate currentLocalDate = LocalDate.now();
//
//        QuitPlanStatus status;
//        if (requestStartDate.isAfter(currentLocalDate)) {
//            status = QuitPlanStatus.NOT_STARTED; // Ngày bắt đầu trong tương lai
//        } else {
//            status = QuitPlanStatus.IN_PROGRESS; // Ngày bắt đầu là hôm nay hoặc trong quá khứ
//        }
//        //ưu tiên status từ request nếu có, nếu không thì dùng status tự động xác định
//        quitPlan.setStatus(request.getStatus() != null ? request.getStatus() : status);
//
//        quitPlan.setStartDate(request.getStartDate());
//        quitPlan.setGoalDate(request.getGoalDate());
//        quitPlan.setInitialSmokingAmount(request.getInitialSmokingAmount());
//
//        QuitPlan savedQuitPlan = quitPlanRepository.save(quitPlan);
//        return convertToResponseDto(savedQuitPlan);
//    }
//
//    @Transactional(readOnly = true)
//    public QuitPlanResponse getQuitPlanById(Integer id) {
//        QuitPlan quitPlan = quitPlanRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kế hoạch cai thuốc với ID: " + id));
//        return convertToResponseDto(quitPlan);
//    }
//
//    @Transactional(readOnly = true)
//    public List<QuitPlanResponse> getAllQuitPlans() {
//        return quitPlanRepository.findAll().stream()
//                .map(this::convertToResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<QuitPlanResponse> getQuitPlansByMemberId(UUID memberId) {
//        // Kiểm tra sự tồn tại của Member trước
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));
//
//        return quitPlanRepository.findByMember(member).stream()
//                .map(this::convertToResponseDto)
//                .collect(Collectors.toList());
//    }
}
