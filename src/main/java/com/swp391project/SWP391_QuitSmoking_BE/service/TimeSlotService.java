package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot.TimeSlotResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.TimeSlot;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final ModelMapper modelMapper;

    // tạo time slot
    @Transactional
    public List<TimeSlotResponseDTO> generateDefaultTimeSlots() {
        List<TimeSlot> generatedSlots = new ArrayList<>();

        // Khung giờ buổi sáng: 8:00 - 12:00
        for (int i = 8; i < 12; i++) {
            LocalTime startTime = LocalTime.of(i, 0);
            LocalTime endTime = LocalTime.of(i + 1, 0);
            // Kiểm tra xem slot đã tồn tại chưa để tránh trùng lặp khi chạy lại
            if (timeSlotRepository.findByStartTime(startTime).isEmpty()) {
                TimeSlot slot = new TimeSlot();
                slot.setLabel(startTime.toString()); // Label là giờ bắt đầu
                slot.setStartTime(startTime);
                slot.setEndTime(endTime);
                slot.setDeleted(false);
                generatedSlots.add(slot);
            }
        }

        // Khung giờ buổi chiều: 13:00 - 17:00
        for (int i = 13; i < 17; i++) {
            LocalTime startTime = LocalTime.of(i, 0);
            LocalTime endTime = LocalTime.of(i + 1, 0);
            if (timeSlotRepository.findByStartTime(startTime).isEmpty()) {
                TimeSlot slot = new TimeSlot();
                slot.setLabel(startTime.toString());
                slot.setStartTime(startTime);
                slot.setEndTime(endTime);
                slot.setDeleted(false);
                generatedSlots.add(slot);
            }
        }

        // Lưu tất cả các slot mới vào cơ sở dữ liệu
        timeSlotRepository.saveAll(generatedSlots);
        return generatedSlots.stream()
                .map(slot -> modelMapper.map(slot, TimeSlotResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Lấy tất cả các time slot đang hoạt động
    public List<TimeSlotResponseDTO> getAllActiveTimeSlots() {
        List<TimeSlot> activeSlots = timeSlotRepository.findByIsDeletedFalseOrderByStartTimeAsc();
        return activeSlots.stream()
                .map(slot -> modelMapper.map(slot, TimeSlotResponseDTO.class))
                .collect(Collectors.toList());
    }

    // cập nhật/xóa mềm một time slot (cho Admin)
    @Transactional
    public TimeSlotResponseDTO toggleTimeSlotDeletion(Integer timeSlotId, boolean isDeleted) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found with ID: " + timeSlotId));

        timeSlot.setDeleted(isDeleted);
        TimeSlot updatedTimeSlot = timeSlotRepository.save(timeSlot);
        return modelMapper.map(updatedTimeSlot, TimeSlotResponseDTO.class);
    }
}
