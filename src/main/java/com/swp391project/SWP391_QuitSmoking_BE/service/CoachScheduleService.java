package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CoachSchedule;
import com.swp391project.SWP391_QuitSmoking_BE.entity.TimeSlot;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CoachRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CoachScheduleRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachScheduleService {
    private final CoachScheduleRepository coachScheduleRepository;
    private final CoachRepository coachRepository; // Cần để tìm Coach entity
    private final TimeSlotRepository timeSlotRepository;
    private final ModelMapper modelMapper;


    /**
     * Coach tạo lịch
     */
    @Transactional
    public List<CoachScheduleResponseDTO> createCoachSchedules(UUID coachId, List<CoachScheduleRequestDTO> requests) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with ID: " + coachId));

        List<CoachSchedule> createdOrUpdatedSchedules = requests.stream()
                .map(req -> {
                    TimeSlot timeSlot = timeSlotRepository.findById(req.getTimeSlotId())
                            .orElseThrow(() -> new ResourceNotFoundException("TimeSlot not found with ID: " + req.getTimeSlotId()));

                    // Kiểm tra xem đã có lịch trình này chưa để tránh trùng lặp
                    Optional<CoachSchedule> existingSchedule = coachScheduleRepository.findByCoachAndScheduleDateAndTimeSlot(
                            coach, req.getScheduleDate(), timeSlot);

                    if (existingSchedule.isPresent()) {
                        // Nếu đã tồn tại, chúng ta bỏ qua việc tạo mới bản ghi này
                        return null;
                    }

                    CoachSchedule newSchedule = new CoachSchedule();
                    newSchedule.setCoach(coach);
                    newSchedule.setTimeSlot(timeSlot);
                    newSchedule.setScheduleDate(req.getScheduleDate());
                    newSchedule.setBooked(false); // Mặc định là chưa được đặt khi tạo mới
                    return newSchedule;
                })
                .filter(java.util.Objects::nonNull) // Lọc bỏ các bản ghi null (trùng lặp)
                .collect(Collectors.toList());

        // Lưu tất cả các lịch trình mới
        List<CoachSchedule> savedSchedules = coachScheduleRepository.saveAll(createdOrUpdatedSchedules);

        return savedSchedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class)) // Sử dụng ModelMapper
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả lịch trình (bao gồm cả đã đặt và chưa đặt) của một Coach
     * @param coachId ID của Coach
     * @return Danh sách CoachScheduleResponseDTO
     */
    public List<CoachScheduleResponseDTO> getMyCoachSchedules(UUID coachId) {
        List<CoachSchedule> schedules = coachScheduleRepository.findByCoach_CoachIdOrderByScheduleDateAscTimeSlot_StartTimeAsc(coachId);
        return schedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class)) // Sử dụng ModelMapper
                .collect(Collectors.toList());
    }

    /**
     * Lấy lịch trình trống của một Coach cụ thể trong một khoảng thời gian
     * @param coachId ID của Coach
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách CoachScheduleResponseDTO trống
     */
    public List<CoachScheduleResponseDTO> getAvailableCoachSchedules(UUID coachId, LocalDate startDate, LocalDate endDate) {
        List<CoachSchedule> availableSchedules = coachScheduleRepository.findByCoach_CoachIdAndIsBookedFalseAndScheduleDateBetweenOrderByScheduleDateAscTimeSlot_StartTimeAsc(
                coachId, startDate, endDate);
        return availableSchedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class)) // Sử dụng ModelMapper
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả lịch trình trống của tất cả các Coach trong một khoảng thời gian (cho admin hoặc member tìm kiếm chung)
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách CoachScheduleResponseDTO trống
     */
    public List<CoachScheduleResponseDTO> getAllAvailableSchedules(LocalDate startDate, LocalDate endDate) {
        List<CoachSchedule> availableSchedules = coachScheduleRepository.findByIsBookedFalseAndScheduleDateBetweenOrderByCoach_FullNameAscScheduleDateAscTimeSlot_StartTimeAsc(
                startDate, endDate);
        return availableSchedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class)) // Sử dụng ModelMapper
                .collect(Collectors.toList());
    }

    /**
     * Xóa lịch trình (chỉ cho Coach của lịch trình đó hoặc Super Admin)
     * @param scheduleId ID của CoachSchedule
     * @param currentUserId ID của người dùng hiện tại (để kiểm tra quyền)
     * @param isAdmin Xác định người dùng có phải Admin/Super Admin không
     */
    @Transactional
    public void softDeleteCoachSchedule(Long scheduleId, UUID currentUserId, boolean isAdmin) {
        CoachSchedule coachSchedule = coachScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("CoachSchedule not found with ID: " + scheduleId));

        // Chỉ cho phép Coach sở hữu lịch hoặc Super Admin xóa
        if (!isAdmin && !coachSchedule.getCoach().getCoachId().equals(currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền xóa lịch trình này.");
        }

        // Không cho phép xóa lịch đã được đặt
        if (coachSchedule.isBooked()) {
            throw new IllegalStateException("Không thể xóa lịch trình đã được đặt.");
        }

        // Đánh dấu lịch trình là đã xóa (có thể thêm trường isDeleted trong CoachSchedule nếu cần)
        coachSchedule.setDeleted(true);
        coachScheduleRepository.save(coachSchedule); // Lưu lại thay đổi
    }

    /**
     * Cập nhật trạng thái đã đặt của lịch trình (dùng trong bước xác nhận đặt lịch)
     * @param scheduleId ID của CoachSchedule
     * @param isBooked Trạng thái đã đặt (true/false)
     * @return CoachSchedule đã cập nhật
     */
    @Transactional
    public CoachSchedule updateCoachScheduleBookingStatus(Long scheduleId, boolean isBooked) {
        CoachSchedule coachSchedule = coachScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("CoachSchedule not found with ID: " + scheduleId));

        coachSchedule.setBooked(isBooked);
        return coachScheduleRepository.save(coachSchedule);
    }

    /**
     * Lấy lịch trình theo ID và đảm bảo nó chưa được đặt (dùng trong bước đặt lịch trong AppointmentService)
     * @param scheduleId ID của CoachSchedule
     * @return CoachSchedule nếu tìm thấy và chưa được đặt, ngược lại ném ngoại lệ
     */
    public CoachSchedule getCoachScheduleByIdAndNotBooked(Long scheduleId) {
        return coachScheduleRepository.findByScheduleIdAndIsBookedFalse(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("CoachSchedule not found or already booked with ID: " + scheduleId));
    }

    /**
     * Lấy lịch trình theo ID (dùng  để lấy lịch trình đã đặt)
     * @param scheduleId ID của CoachSchedule
     * @return CoachSchedule nếu tìm thấy, ngược lại ném ngoại lệ
     */
    public CoachSchedule getCoachScheduleById(Long scheduleId) {
        return coachScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("CoachSchedule not found with ID: " + scheduleId));

    }

    /*
    * Lấy lịch trình đã đặt của một Coach
    * @param coachId ID của Coach
    * @return Danh sách CoachScheduleResponseDTO đã đặt
    */
    public List<CoachScheduleResponseDTO> getBookedCoachSchedules(UUID coachId) {
        List<CoachSchedule> bookedSchedules = coachScheduleRepository.findByCoach_CoachIdAndIsBookedTrueOrderByScheduleDateAscTimeSlot_StartTimeAsc(coachId);
        return bookedSchedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class)) // Sử dụng ModelMapper
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả lịch trình của một Coach trong một khoảng thời gian có phân trang
     * @param coachId ID của Coach
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param pageable Thông tin phân trang (số trang, kích thước trang, sắp xếp)
     * @return Trang của CoachScheduleResponseDTO
     */
    public Page<CoachScheduleResponseDTO> getMyCoachSchedulesPaged(UUID coachId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<CoachSchedule> schedulesPage = coachScheduleRepository.findByCoach_CoachIdAndScheduleDateBetweenOrderByScheduleDateAscTimeSlot_StartTimeAsc(
                coachId, startDate, endDate, pageable);
        return schedulesPage.map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class));
    }

    /**
     * Lấy lịch trình trống của một Coach trong một khoảng thời gian có phân trang
     * @param coachId ID của Coach
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param pageable Thông tin phân trang
     * @return Trang của CoachScheduleResponseDTO trống
     */
    public Page<CoachScheduleResponseDTO> getAvailableCoachSchedulesPaged(UUID coachId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<CoachSchedule> availableSchedulesPage = coachScheduleRepository.findByCoach_CoachIdAndIsBookedFalseAndScheduleDateBetweenOrderByScheduleDateAscTimeSlot_StartTimeAsc(
                coachId, startDate, endDate, pageable);
        return availableSchedulesPage.map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class));
    }

    /**
     * Lấy tất cả lịch trình trống của TẤT CẢ các Coach trong một khoảng thời gian có phân trang
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param pageable Thông tin phân trang
     * @return Trang của CoachScheduleResponseDTO trống
     */
    public Page<CoachScheduleResponseDTO> getAllAvailableSchedulesPaged(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<CoachSchedule> availableSchedulesPage = coachScheduleRepository.findByIsBookedFalseAndScheduleDateBetweenOrderByCoach_FullNameAscScheduleDateAscTimeSlot_StartTimeAsc(
                startDate, endDate, pageable);
        return availableSchedulesPage.map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class));
    }

    /**
     * Lấy lịch hẹn sắp tới của một Coach (1 hoặc 2 lịch gần nhất)
     * @param coachId ID của Coach
     * @param limit Số lượng lịch hẹn muốn lấy (ví dụ: 1 hoặc 2)
     * @return Danh sách các CoachScheduleResponseDTO sắp tới
     */
    public List<CoachScheduleResponseDTO> getUpcomingCoachSchedules(UUID coachId, int limit) {
        // lấy lịch từ ngày hiện tại trở đi, sắp xếp và chỉ lấy 'limit' bản ghi đầu tiên
        Pageable pageable = PageRequest.of(0, limit, Sort.by("scheduleDate", "timeSlot.startTime").ascending());
        List<CoachSchedule> upcomingSchedules = coachScheduleRepository.findByCoach_CoachIdAndIsBookedFalseAndScheduleDateGreaterThanEqualOrderByScheduleDateAscTimeSlot_StartTimeAsc(
                coachId, LocalDate.now(), pageable);
        return upcomingSchedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class))
                .collect(Collectors.toList());
    }
}
