package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot.TimeSlotResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.TimeSlot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeSlotMapperConfig {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configure() {
        modelMapper.createTypeMap(TimeSlot.class, TimeSlotResponseDTO.class);
    }
}
