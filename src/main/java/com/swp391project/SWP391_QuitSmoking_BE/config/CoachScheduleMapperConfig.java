package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CoachSchedule;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoachScheduleMapperConfig {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configure() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);


        modelMapper.createTypeMap(User.class, CoachSimpleResponseDTO.class)
                .addMapping(User::getUserId, CoachSimpleResponseDTO::setCoachId)
                .addMapping(User::getUsername, CoachSimpleResponseDTO::setUsername)
                .addMapping(User::getEmail, CoachSimpleResponseDTO::setEmail);

        modelMapper.createTypeMap(Coach.class, CoachSimpleResponseDTO.class)
                .addMapping(src -> src.getUser().getUserId(), CoachSimpleResponseDTO::setCoachId) // CoachId lấy từ User
                .addMapping(src -> src.getUser().getUsername(), CoachSimpleResponseDTO::setUsername)
                .addMapping(src -> src.getUser().getEmail(), CoachSimpleResponseDTO::setEmail)
                .addMapping(Coach::getFullName, CoachSimpleResponseDTO::setFullName);

        // Ánh xạ từ CoachSchedule Entity sang CoachScheduleResponseDTO
        modelMapper.createTypeMap(CoachSchedule.class, CoachScheduleResponseDTO.class)
                // ModelMapper sẽ tự động ánh xạ 'coach' nếu có TypeMap từ Coach -> CoachSimpleResponseDTO
                // Bạn KHÔNG cần addMapping thủ công cho từng thuộc tính con của coach nữa
                // Chỉ cần thêm mapping nếu tên trường không khớp hoặc cần logic phức tạp
                .addMapping(src -> src.getScheduleId(), CoachScheduleResponseDTO::setScheduleId) // Ví dụ: nếu tên không khớp
                .addMapping(src -> src.isBooked(), CoachScheduleResponseDTO::setBooked) // Nếu getter là isBooked() và setter là setBooked()
                .addMapping(src -> src.getScheduleDate(), CoachScheduleResponseDTO::setScheduleDate)
                .addMapping(src -> src.getCreatedAt(), CoachScheduleResponseDTO::setCreatedAt)
                .addMapping(src -> src.getUpdatedAt(), CoachScheduleResponseDTO::setUpdatedAt);
        // Các trường khác như timeSlot cũng sẽ được ánh xạ tự động nếu có TypeMap TimeSlot -> TimeSlotResponseDTO

    }
}
