package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.plan.PlanRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.plan.PlanResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Plan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.PaidPlanType;
import com.swp391project.SWP391_QuitSmoking_BE.repository.PlanRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;

    // Hàm này sẽ được gọi tự động sau khi PlanService được khởi tạo
    @PostConstruct
    @Transactional
    public void initializeDefaultPlans() {
        // Log để biết hàm được gọi
        System.out.println("Initializing default plans...");

        // Duyệt qua tất cả các loại gói đã định nghĩa trong enum PaidPlanType
        for (PaidPlanType type : PaidPlanType.values()) {
            // Kiểm tra xem gói với loại này đã tồn tại trong DB chưa
            Optional<Plan> existingPlan = planRepository.findByPlanName(type);

            if (existingPlan.isEmpty()) {
                // Nếu chưa tồn tại, tạo một Plan mới
                Plan newPlan = new Plan();
                newPlan.setPlanName(type);
                newPlan.setDescription("Mô tả cho gói " + type.getDisplayName()); // Sử dụng displayName từ enum
                newPlan.setDurationValue(type.getDurationDays()); // Sử dụng durationDays từ enum
                newPlan.setCreatedAt(LocalDateTime.now());

                // Đặt giá mặc định cho từng loại gói
                switch (type) {
                    case STANDARD_14D:
                        newPlan.setPrice(new BigDecimal("119000.00"));
                        break;
                    case POPULAR_30D:
                        newPlan.setPrice(new BigDecimal("209000.00"));
                        break;
                    case SUPER_90D:
                        newPlan.setPrice(new BigDecimal("499000.00"));
                        break;
                    default:
                        newPlan.setPrice(new BigDecimal("0.00")); // Giá mặc định nếu có loại gói mới chưa được định nghĩa
                }

                planRepository.save(newPlan);
                System.out.println("Created default plan: " + type.getDisplayName());
            } else {
                System.out.println("Default plan already exists: " + type.getDisplayName());
            }
        }
        System.out.println("Default plan initialization complete.");
    }

    // Create new plan
    @Transactional
    public PlanResponseDTO createPlan(PlanRequestDTO planRequestDTO) {
        if (planRepository.findByPlanName(planRequestDTO.getPlanName()).isPresent()) {
            throw new IllegalArgumentException("Plan with this name already exists");
        }

        Plan plan = mapToPlanEntity(planRequestDTO);
        plan.setCreatedAt(LocalDateTime.now());
//        plan.setUpdatedAt(LocalDateTime.now());
        Plan savedPlan = planRepository.save(plan);

        return mapToPlanResponseDTO(savedPlan);
    }

    // get all plans
    @Transactional
    public List<PlanResponseDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::mapToPlanResponseDTO)
                .collect(Collectors.toList());
    }

    // get plan by id
    @Transactional
    public PlanResponseDTO getPlanById(Integer planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói với ID: " + planId));
        return mapToPlanResponseDTO(plan);
    }

    // update plan
    @Transactional
    public PlanResponseDTO updatePlan(Integer planId, PlanRequestDTO planRequestDTO) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói với ID: " + planId));

        // Kiểm tra tên gói nếu có thay đổi và tên mới đã tồn tại
        if (!existingPlan.getPlanName().equals(planRequestDTO.getPlanName())) {
            if (planRepository.findByPlanName(planRequestDTO.getPlanName()).isPresent()) {
                throw new IllegalArgumentException("Tên gói đã tồn tại: " + planRequestDTO.getPlanName());
            }
        }
        // Update fields
        existingPlan.setPlanName(planRequestDTO.getPlanName());
        existingPlan.setDescription(planRequestDTO.getDescription());
        existingPlan.setPrice(planRequestDTO.getPrice());
        existingPlan.setDurationValue(planRequestDTO.getDurationValue());
        existingPlan.setUpdatedAt(LocalDateTime.now());

        Plan updatedPlan = planRepository.save(existingPlan);
        return mapToPlanResponseDTO(updatedPlan);
    }



    // Phương thức chuyển đổi từ Entity sang Response DTO
    private PlanResponseDTO mapToPlanResponseDTO(Plan plan) {
        return PlanResponseDTO.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .durationValue(plan.getDurationValue())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    // Phương thức chuyển đổi từ Request DTO sang Entity
    private Plan mapToPlanEntity(PlanRequestDTO requestDTO) {
        return Plan.builder()
                .planName(requestDTO.getPlanName())
                .description(requestDTO.getDescription())
                .price(requestDTO.getPrice())
                .durationValue(requestDTO.getDurationValue())
                .build();
    }
}
