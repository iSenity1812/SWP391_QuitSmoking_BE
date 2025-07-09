package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentToResponseDTOConverter implements Converter<Appointment, AppointmentResponseDTO> {

    private final ModelMapper modelMapper; // Inject ModelMapper để map các đối tượng lồng nhau

    @Override
    public AppointmentResponseDTO convert(MappingContext<Appointment, AppointmentResponseDTO> context) {
        Appointment source = context.getSource();
        if (source == null) {
            return null;
        }

        AppointmentResponseDTO destination = new AppointmentResponseDTO();

        // Ánh xạ các thuộc tính trực tiếp (primitive/LocalDateTime/Enum)
        destination.setAppointmentId(source.getAppointmentId());
        destination.setStatus(source.getStatus());
        destination.setNote(source.getNote());
        destination.setBookingTime(source.getBookingTime());
//        destination.setCreatedAt(source.getCreatedAt());
//        destination.setUpdatedAt(source.getUpdatedAt());

        // *** Ánh xạ các đối tượng lồng nhau một cách TƯỜNG MINH bằng modelMapper ***
        // Điều này đảm bảo chúng ta kiểm soát hoàn toàn
        if (source.getMember() != null) {
            // Gọi ModelMapper để ánh xạ Member -> UserSimpleResponseDTO
            // ModelMapper sẽ sử dụng TypeMap đã định nghĩa cho Member -> UserSimpleResponseDTO
            destination.setMember(modelMapper.map(source.getMember(), UserSimpleResponseDTO.class));
        }

        if (source.getCoachSchedule() != null) {
            // Gọi ModelMapper để ánh xạ CoachSchedule -> CoachScheduleResponseDTO
            // ModelMapper sẽ sử dụng TypeMap đã định nghĩa cho CoachSchedule -> CoachScheduleResponseDTO
            destination.setCoachSchedule(modelMapper.map(source.getCoachSchedule(), CoachScheduleResponseDTO.class));
        }

        return destination;
    }
}