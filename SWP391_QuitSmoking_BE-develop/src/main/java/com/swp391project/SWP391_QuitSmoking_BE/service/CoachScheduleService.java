package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentDetailsDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.*;
import com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot.RegisteredSlotDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Appointment;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CoachSchedule;
import com.swp391project.SWP391_QuitSmoking_BE.entity.TimeSlot;
import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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

        // Kiểm tra xem có phaải là quá khứ không (nếu mà là quá khứ thì không cho tạo lịch (thời gian cụ thể ngày, giờ, phút,...)
        LocalDate today = LocalDate.now();
        for (CoachScheduleRequestDTO request : requests) {
            if (request.getScheduleDate().isBefore(today)) {
                throw new IllegalArgumentException("Không thể tạo lịch trình trong quá khứ: " + request.getScheduleDate());
            }
        }

        List<CoachSchedule> schedulesToSave = new ArrayList<>(); // Sử dụng List để dễ dàng thêm/thay đổi đối tượng

        for (CoachScheduleRequestDTO req : requests) {
            TimeSlot timeSlot = timeSlotRepository.findById(req.getTimeSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("TimeSlot not found with ID: " + req.getTimeSlotId()));

            Optional<CoachSchedule> existingScheduleOptional = coachScheduleRepository.findByCoachAndScheduleDateAndTimeSlot(
                    coach, req.getScheduleDate(), timeSlot);

            if (existingScheduleOptional.isPresent()) {
                CoachSchedule existingSchedule = existingScheduleOptional.get();

                if (existingSchedule.isDeleted()) {
                    // Cập nhật bản ghi đã soft-delete
                    existingSchedule.setDeleted(false);
                    existingSchedule.setBooked(false); // Đặt lại trạng thái chưa booked
                    existingSchedule.setUpdatedAt(LocalDateTime.now());
                    schedulesToSave.add(existingSchedule); // Thêm bản ghi đã cập nhật vào list
                } else {
                    // Nếu lịch trình chưa bị xóa (đang hoạt động), ném lỗi
                    throw new IllegalArgumentException(
                            "Lịch trình cho khung giờ " + timeSlot.getStartTime() + " - " + timeSlot.getEndTime() +
                                    " vào ngày " + req.getScheduleDate() + " đã tồn tại và đang hoạt động.");
                }
            } else {
                // Nếu chưa tồn tại, tạo mới hoàn toàn
                CoachSchedule newSchedule = new CoachSchedule();
                newSchedule.setCoach(coach);
                newSchedule.setTimeSlot(timeSlot);
                newSchedule.setScheduleDate(req.getScheduleDate());
                newSchedule.setBooked(false);
                newSchedule.setDeleted(false);
                newSchedule.setCreatedAt(LocalDateTime.now());
                schedulesToSave.add(newSchedule); // Thêm bản ghi mới vào list
            }
        }
        // Lưu tất cả các lịch trình mới
        List<CoachSchedule> savedSchedules = coachScheduleRepository.saveAll(schedulesToSave);

        return savedSchedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class)) // Sử dụng ModelMapper
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả lịch trình (bao gồm cả đã đặt và chưa đặt) của một Coach
     * @param coachId ID của Coach
     * @return Danh sách CoachScheduleResponseDTO
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public CoachSchedule getCoachScheduleById(Long scheduleId) {
        return coachScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("CoachSchedule not found with ID: " + scheduleId));

    }

    /*
    * Lấy lịch trình đã đặt của một Coach
    * @param coachId ID của Coach
    * @return Danh sách CoachScheduleResponseDTO đã đặt
    */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<CoachScheduleResponseDTO> getUpcomingCoachSchedules(UUID coachId, int limit) {
        // lấy lịch từ ngày hiện tại trở đi, sắp xếp và chỉ lấy 'limit' bản ghi đầu tiên
        Pageable pageable = PageRequest.of(0, limit, Sort.by("scheduleDate", "timeSlot.startTime").ascending());
        List<CoachSchedule> upcomingSchedules = coachScheduleRepository.findByCoach_CoachIdAndIsBookedFalseAndScheduleDateGreaterThanEqualOrderByScheduleDateAscTimeSlot_StartTimeAsc(
                coachId, LocalDate.now(), pageable);
        return upcomingSchedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public WeeklyScheduleResponseDTO getWeeklyScheduleForCoach(UUID coachId, LocalDate dateInWeek) {
        // Tìm ngày đầu tuần (thứ Hai) và cuối tuần (Chủ Nhật)
        LocalDate weekStart = dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = dateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Lấy tất cả CoachSchedule cho coach trong tuần đó với eager loading các mối quan hệ
        List<CoachSchedule> rawCoachSchedules = coachScheduleRepository.findByCoachIdAndScheduleDateBetweenWithDetails(
                coachId, weekStart, weekEnd);


        // Map CoachSchedule theo ngày và TimeSlot để xử lý các bản ghi trùng lặp do JOIN FETCH
        // Sử dụng Map<LocalDate, Map<Integer, CoachSchedule>> để dễ dàng tìm kiếm slot theo ngày và timeslotId
        Map<LocalDate, Map<Integer, CoachSchedule>> schedulesByDateAndTimeSlot = rawCoachSchedules.stream()
                .collect(Collectors.groupingBy(
                        CoachSchedule::getScheduleDate,
                        Collectors.toMap(
                                cs -> cs.getTimeSlot().getTimeSlotId(), //
                                cs -> cs,
                                (existing, replacement) -> existing // Xử lý các bản ghi trùng lặp do LEFT JOIN FETCH với @OneToMany
                        )
                ));

        List<RegisteredSlotDTO> registeredSlots = new ArrayList<>();

        // Trong trường hợp này, chúng ta sẽ chỉ tạo RegisteredSlotDTO cho các CoachSchedule đã đăng ký.
        // Để đảm bảo thứ tự, chúng ta sẽ lặp qua các ngày và sau đó các timeslot có sẵn.
        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            Map<Integer, CoachSchedule> dailySchedules = schedulesByDateAndTimeSlot.getOrDefault(date, java.util.Collections.emptyMap());

            // Lấy tất cả TimeSlots cho ngày hiện tại từ các CoachSchedule đã tìm thấy
            // Nếu bạn có một danh sách TimeSlot chuẩn, bạn có thể lặp qua đó để đảm bảo tất cả slot đều được hiển thị
            // Kể cả những slot mà coach chưa đăng ký.
            // Hiện tại, chúng ta chỉ tạo slot DTO cho những coachSchedule thực sự có trong DB.
            List<CoachSchedule> sortedDailySchedules = dailySchedules.values().stream()
                    .sorted(Comparator.comparing(cs -> cs.getTimeSlot().getStartTime()))
                    .collect(Collectors.toList());

            for (CoachSchedule coachSchedule : sortedDailySchedules) {
                RegisteredSlotDTO slotDto = new RegisteredSlotDTO();
                slotDto.setCoachScheduleId(coachSchedule.getScheduleId().intValue()); // Chuyển Long sang Integer nếu cần
                slotDto.setDate(coachSchedule.getScheduleDate());
                slotDto.setTimeSlotId(coachSchedule.getTimeSlot().getTimeSlotId());
                slotDto.setLabel(coachSchedule.getTimeSlot().getLabel());
                slotDto.setStartTime(coachSchedule.getTimeSlot().getStartTime().toString());
                slotDto.setEndTime(coachSchedule.getTimeSlot().getEndTime().toString());

                // Logic cho isAvailable: Một slot được coi là "available" nếu KHÔNG CÓ cuộc hẹn CONFIRMED nào.
                boolean hasConfirmedAppointment = coachSchedule.getAppointments().stream()
                        .anyMatch(apt -> apt.getStatus() == AppointmentStatus.CONFIRMED);
                slotDto.setAvailable(!hasConfirmedAppointment);

                // Lấy TẤT CẢ các cuộc hẹn liên quan đến CoachSchedule này
                List<AppointmentDetailsDTO> appointmentDetailsList = coachSchedule.getAppointments().stream()
                        .map(apt -> {
                            AppointmentDetailsDTO aptDetailsDto = new AppointmentDetailsDTO();
                            aptDetailsDto.setAppointmentId(apt.getAppointmentId());
                            aptDetailsDto.setStatus(apt.getStatus());
                            aptDetailsDto.setNotes(apt.getNote());

                            if (apt.getMember() != null && apt.getMember().getUser() != null) {
                                aptDetailsDto.setClientId(apt.getMember().getMemberId()); // MemberID của Member
                                aptDetailsDto.setClientName(apt.getMember().getUser().getUsername()); // Giả sử User có fullName
                            }
                            return aptDetailsDto;
                        })
                        // Sắp xếp các cuộc hẹn nếu cần (ví dụ theo booking_time)
                        .sorted(Comparator.comparing(AppointmentDetailsDTO::getAppointmentId)) // Sắp xếp theo ID hoặc booking_time
                        .collect(Collectors.toList());

                slotDto.setAppointmentDetails(appointmentDetailsList); // Set List of appointments
                registeredSlots.add(slotDto);
            }
        }
        return WeeklyScheduleResponseDTO.builder()
                .weekStartDate(weekStart)
                .weekEndDate(weekEnd)
                .registeredSlots(registeredSlots)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CoachScheduleResponseDTO> findAvailableSchedulesTodayByTimeRange(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        List<TimeSlot> relevantTimeSlots = timeSlotRepository
                .findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(startTime, endTime);
        if (relevantTimeSlots.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy các khung giờ phù hợp trong khoảng " + startTime + " - " + endTime);
        }

        List<CoachSchedule> schedules = coachScheduleRepository.findAvailableSchedulesByDateAndTimeRange(
                date, startTime, endTime);

        return schedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class)) // Sử dụng ModelMapper
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm lịch rảnh của tất cả các Coach theo khoảng giờ trong một khoảng ngày (tối đa 2 tuần).
     * @param searchRequest DTO chứa startDate, endDate, startTime, endTime.
     * @return Danh sách CoachScheduleResponseDTO rảnh.
     */
    @Transactional(readOnly = true)
    public List<CoachScheduleResponseDTO> findAvailableSchedulesByDateRangeAndTimeRange(
            AvailableScheduleSearchRequestDTO searchRequest) {
        LocalDate startDate = searchRequest.getStartDate();
        LocalDate endDate = searchRequest.getEndDate();
        LocalTime startTime = searchRequest.getStartTime();
        LocalTime endTime = searchRequest.getEndTime();

        // Validations
        if (startDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống.");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Giờ bắt đầu và giờ kết thúc không được để trống.");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Giờ bắt đầu không thể sau giờ kết thúc.");
        }
        // Nếu endDate không được cung cấp, mặc định là startDate
        if (endDate == null) {
            endDate = startDate;
        }

        // Giới hạn khoảng ngày tối đa 2 tuần
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc.");
        }
        if (startDate.plusWeeks(2).isBefore(endDate)) {
            throw new IllegalArgumentException("Khoảng thời gian tìm kiếm không được vượt quá 2 tuần.");
        }
        // Kiểm tra xem TimeSlots có tồn tại với `startTime` và `endTime` đã cho không
        // Tương tự như trên, bạn cần TimeSlotRepository để xác thực.
        timeSlotRepository.findByStartTime(startTime)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy TimeSlot với thời gian bắt đầu: " + startTime));
        timeSlotRepository.findByEndTime(endTime)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy TimeSlot với thời gian kết thúc: " + endTime));

        List<CoachSchedule> schedules = coachScheduleRepository.findAvailableSchedulesByDateRangeAndTimeRange(
                startDate, endDate, startTime, endTime);

        return schedules.stream()
                .map(schedule -> modelMapper.map(schedule, CoachScheduleResponseDTO.class)) // Sử dụng ModelMapper
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CoachScheduleResponseDTO> findAvailableSchedulesByDateAndTimeSlot(SearchAvailableScheduleBySlotDTO request) {
        // end date ko được truước start date
        if (request.getStartDate() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống.");
        }

        // Nếu endDate không được cung cấp, mặc định là startDate
        if (request.getEndDate() == null) {
            request.setEndDate(request.getStartDate());
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc.");
        }

        // Nếu timeslot rỗng -> chuyển sang null
        List<Integer> timeSlotIds = request.getTimeSlotIds();
        if (timeSlotIds == null || timeSlotIds.isEmpty()) {
            timeSlotIds = null; // Chuyển sang null nếu không có timeslot nào được chọn
        }
        
        List<CoachSchedule> schedules = coachScheduleRepository.findAvailableSchedulesByDateRangeAndTimeslots(
                request.getStartDate(), request.getEndDate(), timeSlotIds);

        return schedules.stream()
                .map(schedule -> {
                    CoachScheduleResponseDTO dto = modelMapper.map(schedule, CoachScheduleResponseDTO.class);
                    if (schedule.getCoach() != null && schedule.getCoach().getUser() != null) {
                        dto.getCoach().setFullName(schedule.getCoach().getFullName());
                        dto.getCoach().setEmail(schedule.getCoach().getUser().getEmail());
                        dto.getCoach().setUsername(schedule.getCoach().getUser().getUsername());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
