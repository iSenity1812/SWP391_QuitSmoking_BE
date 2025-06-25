package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CoachSchedule;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoachScheduleMapperConfig {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configure() {

        // User -> UserSimpleResponseDTO
        modelMapper.createTypeMap(User.class, UserSimpleResponseDTO.class)
                .addMapping(User::getUserId, UserSimpleResponseDTO::setUserId)
                .addMapping(User::getUsername, UserSimpleResponseDTO::setUsername)
                .addMapping(User::getEmail, UserSimpleResponseDTO::setEmail);

        // Coach -> CoachSimpleResponseDTO
        modelMapper.createTypeMap(Coach.class, CoachSimpleResponseDTO.class)
                .addMapping(src -> src.getUser().getUserId(), CoachSimpleResponseDTO::setCoachId) // CoachId lấy từ User
                .addMapping(src -> src.getUser().getUsername(), CoachSimpleResponseDTO::setUsername)
                .addMapping(src -> src.getUser().getEmail(), CoachSimpleResponseDTO::setEmail)
                .addMapping(Coach::getFullName, CoachSimpleResponseDTO::setFullName);

        modelMapper.createTypeMap(CoachScheduleResponseDTO.class, CoachSimpleResponseDTO.class)
                        .addMapping(src -> src.getCoach().getCoachId(), CoachSimpleResponseDTO::setCoachId)
                        .addMapping(src -> src.getCoach().getUsername(), CoachSimpleResponseDTO::setUsername)
                        .addMapping(src -> src.getCoach().getEmail(), CoachSimpleResponseDTO::setEmail)
                        .addMapping(src -> src.getCoach().getFullName(), CoachSimpleResponseDTO::setFullName)
                        .addMapping(src -> src.getCoach().getRating(), CoachSimpleResponseDTO::setRating)
                        .addMapping(src -> src.getCoach().getSpecialties(), CoachSimpleResponseDTO::setSpecialties);

        // Ánh xạ từ CoachSchedule Entity sang CoachScheduleResponseDTO
        modelMapper.createTypeMap(CoachSchedule.class, CoachScheduleResponseDTO.class)
                .addMapping(CoachSchedule::getScheduleId, CoachScheduleResponseDTO::setScheduleId) // Ví dụ: nếu tên không khớp
                .addMapping(CoachSchedule::isBooked, CoachScheduleResponseDTO::setBooked) // Nếu getter là isBooked() và setter là setBooked()
                .addMapping(CoachSchedule::getScheduleDate, CoachScheduleResponseDTO::setScheduleDate);
//                .addMapping(CoachSchedule::getCreatedAt, CoachScheduleResponseDTO::setCreatedAt)
//                .addMapping(CoachSchedule::getUpdatedAt, CoachScheduleResponseDTO::setUpdatedAt);

        // Các trường khác như timeSlot cũng sẽ được ánh xạ tự động nếu có TypeMap TimeSlot -> TimeSlotResponseDTO

    }
}
