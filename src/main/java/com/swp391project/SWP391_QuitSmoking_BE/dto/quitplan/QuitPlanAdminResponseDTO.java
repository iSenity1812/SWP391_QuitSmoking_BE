package com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan;

import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QuitPlanAdminResponseDTO {
    private Integer quitPlanId;
    private UUID memberId; // ID của thành viên sở hữu kế hoạch
    private String memberUsername; // Tên người dùng của thành viên (để admin dễ quản lý)
//    private Integer planTypeId; // ID của loại kế hoạch (admin có thể muốn nhìn ID gốc)
//    private String planTypeName; // Tên loại kế hoạch
    private ReductionQuitPlanType reductionType;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDate goalDate;
    private int initialSmokingAmount;
    private BigDecimal pricePerPack;
    private int cigarettesPerPack;
    private QuitPlanStatus status;
}
