package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentUserResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Appointment;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapperConfig {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configure() {



        // Member -> UserSimpleResponseDTO
        modelMapper.createTypeMap(Member.class, UserSimpleResponseDTO.class)
                .addMapping(src -> src.getUser().getUserId(), UserSimpleResponseDTO::setUserId)
                .addMapping(src -> src.getUser().getUsername(), UserSimpleResponseDTO::setUsername)
                .addMapping(src -> src.getUser().getEmail(), UserSimpleResponseDTO::setEmail);

        // Ánh xạ từ Appointment -> AppointmentResponseDTO
        modelMapper.createTypeMap(Appointment.class, AppointmentResponseDTO.class)
                .addMapping(Appointment::getAppointmentId, AppointmentResponseDTO::setAppointmentId)
                .addMapping(Appointment::getStatus, AppointmentResponseDTO::setStatus)
                .addMapping(Appointment::getNote, AppointmentResponseDTO::setNote)
                .addMapping(Appointment::getBookingTime, AppointmentResponseDTO::setBookingTime);
        ;
//                .addMapping(Appointment::getCreatedAt, AppointmentResponseDTO::setCreatedAt)
//                .addMapping(Appointment::getUpdatedAt, AppointmentResponseDTO::setUpdatedAt);

        modelMapper.createTypeMap(Appointment.class, AppointmentUserResponseDTO.class)
                .addMapping(Appointment::getAppointmentId, AppointmentUserResponseDTO::setAppointmentId)
                .addMapping(Appointment::getStatus, AppointmentUserResponseDTO::setStatus)
                .addMapping(Appointment::getNote, AppointmentUserResponseDTO::setNote)
                .addMapping(Appointment::getBookingTime, AppointmentUserResponseDTO::setBookingTime)
                .addMapping(src -> src.getCoachSchedule().getScheduleDate(), AppointmentUserResponseDTO::setScheduleDate)
                .addMapping(src -> src.getCoachSchedule().getTimeSlot(), AppointmentUserResponseDTO::setTimeSlot)
                // userSimpleResponseDTO
                .addMapping(src -> src.getCoachSchedule().getCoach().getFullName(), AppointmentUserResponseDTO::setFullName)
                .addMapping(src -> src.getCoachSchedule().getCoach().getUser().getEmail(), AppointmentUserResponseDTO::setEmail);
    }
}
