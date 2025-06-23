package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanAdminResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuitPlanMapperConfig {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configure() {
        // Cấu hình ánh xạ từ QuitPlan Entity sang QuitPlanResponseDTO
        modelMapper.createTypeMap(QuitPlan.class, QuitPlanResponseDTO.class)
                .addMappings(mapper -> {});

        // Cấu hình ánh xạ từ QuitPlan sang QuitPlanAdminResponseDTO
        modelMapper.createTypeMap(QuitPlan.class, QuitPlanAdminResponseDTO.class)
                .addMapping(src -> src.getMember().getMemberId(), QuitPlanAdminResponseDTO::setMemberId)
                .addMapping(src -> src.getMember().getUser().getUsername(), QuitPlanAdminResponseDTO::setMemberUsername);

        // Cấu hình ánh xạ từ QuitPlanResponseDTO sang QuitPlan

    }
}
