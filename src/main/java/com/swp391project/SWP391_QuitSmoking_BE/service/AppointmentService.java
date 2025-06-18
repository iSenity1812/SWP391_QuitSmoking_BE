package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentUserResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AppointmentRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CoachRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CoachScheduleRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final MemberRepository memberRepository;
    private final CoachRepository coachRepository;
    private ModelMapper modelMapper;
    private final CoachScheduleRepository coachScheduleRepository; // Cần để cập nhật trạng thái CoachSchedule

    @Transactional
    public AppointmentResponseDTO createAppointment(UUID callerId, List<Role> callerRoles, AppointmentRequestDTO request) {
        Member memberToBook;
        Coach coachOfSchedule = null;

        // Xác định người gọi
        if (callerRoles.contains(Role.PREMIUM_MEMBER)) {
            // Nếu là member -> chỉ cho phép đặt lịch của chính mình
            if (request.getMemberIdToBook() != null && !request.getMemberIdToBook().equals(callerId)) {
                throw new AccessDeniedException("Bạn không có quyền đặt lịch cho thành viên khác.");
            }
            memberToBook = memberRepository.findById(callerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + callerId));
        } else if (callerRoles.contains(Role.COACH))  {
            if (request.getMemberIdToBook() != null) {

            } else if (request.getEmail() != null ) {

            }
        } else {
            throw new AccessDeniedException("Bạn không có quyền đặt lịch hẹn. Chỉ có Premium Member hoặc Coach mới có thể đặt lịch.");
        }


//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));
//
        CoachSchedule coachSchedule = coachScheduleRepository.findById(request.getCoachScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("CoachSchedule not found with ID: " + request.getCoachScheduleId()));
//
//        // Kiểm tra xem lịch trình đã bị đặt chưa
        if (coachSchedule.isBooked()) {
            throw new IllegalStateException("Lịch trình này đã được đặt. Vui lòng chọn lịch khác.");
        }
//        // Tạo Appointment
        Appointment appointment = new Appointment();
        appointment.setMember(memberToBook);
        appointment.setCoachSchedule(coachSchedule);
        appointment.setStatus(AppointmentStatus.CONFIRMED); // Mặc định là CONFIRMED khi đặt thành công
        appointment.setNote(request.getNote());
        appointment.setBookingTime(LocalDateTime.now());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
//
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Cập nhật trạng thái isBooked của CoachSchedule
        coachSchedule.setBooked(true);
        coachScheduleRepository.save(coachSchedule);

        return modelMapper.map(savedAppointment, AppointmentResponseDTO.class);
    }

    /**
     * Hủy một lịch hẹn
     * @param appointmentId Id của lịch hẹn
     * @param currentUserId ID của người dùng hiện tại (để kiểm tra quyền)
     * @param isAdmin xác định người dùng có phải admin không
     * @return AppointmentResponseDTO đã được hủy
     */
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long appointmentId, UUID currentUserId, boolean isAdmin) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));


        UUID memberId = appointment.getMember().getMemberId();
        UUID coachId = appointment.getCoachSchedule().getCoach().getCoachId();

        boolean isMemberCancelling = memberId.equals(currentUserId);
        boolean isCoachCancelling = coachId.equals(currentUserId);

        // Kiểm tra quyền truy cập
        // Admin có thể hủy bất kỳ lịch hẹn nào
        // Coach có thể hủy bất kỳ lịch hẹn nào của mình
        // Member chỉ có thể hủy lịch hẹn của chính mình nếu còn hơn 2 tiếng truớc thời gian hẹn
        if (!isAdmin && !isCoachCancelling && !isMemberCancelling) {
            throw new AccessDeniedException("Bạn không có quyền hủy lịch hẹn này.");
        }

        // Lấy thời gian hiện tại và thời gian hẹn
        LocalDate scheduleDate = appointment.getCoachSchedule().getScheduleDate();
        LocalTime startTime = appointment.getCoachSchedule().getTimeSlot().getStartTime();
        LocalDateTime appointmentTime = LocalDateTime.of(scheduleDate, startTime);

        // Thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Nếu member -> ktra quy tắc 2h
        if (isMemberCancelling && !isAdmin) {
            long hourUntilAppointment = Duration.between(now, appointmentTime).toHours();
            if (hourUntilAppointment < 2) {
                throw new IllegalStateException("Bạn chỉ có thể hủy lịch hẹn ít nhất 2 tiếng trước thời gian hẹn.");
            }
        }

        // Kiểm tra trạng thái hiện tại của lịch hẹn
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
            appointment.getStatus() == AppointmentStatus.COMPLETED ||
            appointment.getStatus() == AppointmentStatus.MISSED) {
            throw new IllegalStateException("Không thể hủy lịch hẹn ở trạng thái hiện tại (" + appointment.getStatus() + ").");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // isBooked của CoachSchedule sẽ được set lại là false
        CoachSchedule coachSchedule = updatedAppointment.getCoachSchedule();
        coachSchedule.setBooked(false);
        coachScheduleRepository.save(coachSchedule);

        return modelMapper.map(updatedAppointment, AppointmentResponseDTO.class);
    }

    /**
     * Update trạng thái lịch hẹn (chỉ Admin hoặc Coach)
     * @param appointmentId Id của lịch hẹn
     * @param newStatus Trạng thái mới
     * @param currentUserId id của người dùng hiện tại (kiểm tra coach/admin)
     * @param isAdmin Là Admin hay không
     * @return AppointmentResponseDTO đã cập nhật
     */
    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus, UUID currentUserId, boolean isAdmin)  {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        boolean isCoach = appointment.getCoachSchedule().getCoach().getCoachId().equals(currentUserId);
        if (!isAdmin && !isCoach) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật trạng thái lịch hẹn này.");
        }

        // 1. Không cho phép chuyển từ CANCELLED sang trạng thái khác
        if (appointment.getStatus() == AppointmentStatus.CANCELLED && newStatus != AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Không thể chuyển trạng thái từ CANCELLED sang trạng thái khác.");
        }

        // 2. Không cho phép chuyển từ COMPLETED hoặc MISSED sang CONFIRMED
        if ((appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.MISSED) && newStatus == AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Không thể chuyển trạng thái từ " + appointment.getStatus() + " sang CONFIRMED.");
        }

        // Nếu trạng thái mới là CANCELLED -> cập nhật trạng thái isBooked của CoachSchedule
        if (newStatus == AppointmentStatus.CANCELLED && appointment.getStatus() != AppointmentStatus.CANCELLED) {
            CoachSchedule coachSchedule = appointment.getCoachSchedule();
            coachSchedule.setBooked(false);
            coachScheduleRepository.save(coachSchedule);
        }

        appointment.setStatus(newStatus);
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(updatedAppointment, AppointmentResponseDTO.class);
    }

    /**
     * Lấy tất cả lịch hẹn của một Member, có phân trang và lọc theo khoảng ngày.
     * @param memberId ID của thành viên.
     * @param startDate Ngày bắt đầu.
     * @param endDate Ngày kết thúc.
     * @param pageable Thông tin phân trang.
     * @return Trang của AppointmentResponseDTO.
     */
    @Transactional(readOnly = true)
    public Page<AppointmentUserResponseDTO> getMemberAppointments(UUID memberId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Appointment> appointmentsPage = appointmentRepository.findByMember_MemberIdAndCoachSchedule_ScheduleDateBetweenOrderByCoachSchedule_ScheduleDateAscCoachSchedule_TimeSlot_StartTimeAsc(
                memberId, startDate, endDate, pageable);
        return appointmentsPage.map(appointment -> modelMapper.map(appointment, AppointmentUserResponseDTO.class));
    }

    /**
     * Lấy tất cả lịch hẹn của một Coach, có phân trang và lọc theo khoảng ngày và trạng thái.
     * @param coachId ID của coach.
     * @param startDate Ngày bắt đầu.
     * @param endDate Ngày kết thúc.
     * @param statuses Danh sách trạng thái muốn lọc (nếu null/empty thì lấy tất cả).
     * @param pageable Thông tin phân trang.
     * @return Trang của AppointmentResponseDTO.
     */
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getCoachAppointments(
            UUID coachId,
            LocalDate startDate,
            LocalDate endDate,
            List<AppointmentStatus> statuses,
            Pageable pageable) {

        // Lấy Page của Appointment từ repository
        Page<Appointment> appointmentsPage = appointmentRepository.findCoachAppointments(
                coachId, startDate, endDate, statuses, pageable);

        // Map từng Appointment sang AppointmentResponseDTO và trả về Page<AppointmentResponseDTO>
        return appointmentsPage.map(appointment -> {
            AppointmentResponseDTO dto = modelMapper.map(appointment, AppointmentResponseDTO.class);

            // Ánh xạ thủ công cho thông tin User của Coach (nếu ModelMapper không tự xử lý)
            // Đoạn này tương tự như trong getUpcomingCoachAppointments
            if (appointment.getCoachSchedule() != null &&
                    appointment.getCoachSchedule().getCoach() != null &&
                    appointment.getCoachSchedule().getCoach().getUser() != null) {

                User coachUser = appointment.getCoachSchedule().getCoach().getUser();
                // Giả sử CoachResponseDTO có trường username và email
                if (dto.getCoachSchedule() != null && dto.getCoachSchedule().getCoach() != null) {
                    dto.getCoachSchedule().getCoach().setUsername(coachUser.getUsername());
                    dto.getCoachSchedule().getCoach().setEmail(coachUser.getEmail());
                }
            }
            return dto;
        });
    }

    /**
     * Lấy tất cả lịch hẹn (cho Admin), có phân trang và lọc theo khoảng ngày và trạng thái.
     * @param startDate Ngày bắt đầu.
     * @param endDate Ngày kết thúc.
     * @param statuses Danh sách trạng thái muốn lọc.
     * @param pageable Thông tin phân trang.
     * @return Trang của AppointmentResponseDTO.
     */
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAllAppointments(LocalDate startDate, LocalDate endDate, List<AppointmentStatus> statuses, Pageable pageable) {
        List<AppointmentStatus> actualStatuses = (statuses != null && !statuses.isEmpty()) ? statuses : Arrays.asList(AppointmentStatus.values());

        Page<Appointment> appointmentsPage = appointmentRepository.findByCoachSchedule_ScheduleDateBetweenAndStatusInOrderByCoachSchedule_Coach_FullNameAscCoachSchedule_ScheduleDateAscCoachSchedule_TimeSlot_StartTimeAsc(
                startDate, endDate, actualStatuses, pageable);
        return appointmentsPage.map(appointment -> {
            AppointmentResponseDTO dto = modelMapper.map(appointment, AppointmentResponseDTO.class);

            // Ánh xạ thủ công cho thông tin User của Coach (nếu ModelMapper không tự xử lý)
            // Đoạn này tương tự như trong getUpcomingCoachAppointments
            if (appointment.getCoachSchedule() != null &&
                    appointment.getCoachSchedule().getCoach() != null &&
                    appointment.getCoachSchedule().getCoach().getUser() != null) {

                User coachUser = appointment.getCoachSchedule().getCoach().getUser();
                // Giả sử CoachResponseDTO có trường username và email
                if (dto.getCoachSchedule() != null && dto.getCoachSchedule().getCoach() != null) {
                    dto.getCoachSchedule().getCoach().setUsername(coachUser.getUsername());
                    dto.getCoachSchedule().getCoach().setEmail(coachUser.getEmail());
                }
            }
            return dto;
        });
    }

    /**
     * Lấy các lịch hẹn sắp tới của một Member.
     * @param memberId ID của thành viên.
     * @param limit Số lượng lịch hẹn muốn lấy.
     * @return Danh sách AppointmentResponseDTO.
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getUpcomingMemberAppointments(UUID memberId, int limit) {
        List<AppointmentStatus> activeStatuses = List.of(AppointmentStatus.CONFIRMED);
        Pageable pageable = PageRequest.of(0, limit, Sort.by("coachSchedule.scheduleDate", "coachSchedule.timeSlot.startTime").ascending());

        List<Appointment> upcomingAppointments = appointmentRepository.findByMember_MemberIdAndCoachSchedule_ScheduleDateGreaterThanEqualAndStatusInOrderByCoachSchedule_ScheduleDateAscCoachSchedule_TimeSlot_StartTimeAsc(
                memberId, LocalDate.now(), activeStatuses, pageable);
        return upcomingAppointments.stream()
//                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDTO.class))
//                .collect(Collectors.toList());
                .map(appointment -> {
                    AppointmentResponseDTO dto = modelMapper.map(appointment, AppointmentResponseDTO.class);

                    // Ánh xạ thủ công để đảm bảo thông tin từ User được lấy đúng
//                    if (appointment.getMember() != null && appointment.getMember().getUser() != null) {
//                        User memberUser = appointment.getMember().getUser();
//                        dto.setMember(new UserSimpleResponseDTO(memberUser.getUsername(), memberUser.getEmail()));
//                    }

                    // Ánh xạ thông tin Coach từ CoachSchedule
                    if (appointment.getCoachSchedule() != null &&
                            appointment.getCoachSchedule().getCoach() != null &&
                            appointment.getCoachSchedule().getCoach().getUser() != null) {

                        User coachUser = appointment.getCoachSchedule().getCoach().getUser();
                        dto.getCoachSchedule().getCoach().setUsername(coachUser.getUsername());
                        dto.getCoachSchedule().getCoach().setEmail(coachUser.getEmail());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Lấy các lịch hẹn sắp tới của một Coach.
     * @param coachId ID của coach.
     * @param limit Số lượng lịch hẹn muốn lấy.
     * @return Danh sách AppointmentResponseDTO.
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getUpcomingCoachAppointments(UUID coachId, int limit) {
        List<AppointmentStatus> activeStatuses = Arrays.asList(AppointmentStatus.CONFIRMED);
        Pageable pageable = PageRequest.of(0, limit, Sort.by("coachSchedule.scheduleDate", "coachSchedule.timeSlot.startTime").ascending());

        List<Appointment> upcomingAppointments = appointmentRepository
                .findUpcomingAppointmentsByCoachIdWithDetails(coachId, LocalDate.now(), activeStatuses, pageable);

        return upcomingAppointments.stream()
                .map(appointment -> {
                    AppointmentResponseDTO dto = modelMapper.map(appointment, AppointmentResponseDTO.class);

                    // Ánh xạ thủ công để đảm bảo thông tin từ User được lấy đúng
                    if (appointment.getCoachSchedule() != null &&
                            appointment.getCoachSchedule().getCoach() != null &&
                            appointment.getCoachSchedule().getCoach().getUser() != null) {

                        User coachUser = appointment.getCoachSchedule().getCoach().getUser();
                        dto.getCoachSchedule().getCoach().setUsername(coachUser.getUsername());
                        dto.getCoachSchedule().getCoach().setEmail(coachUser.getEmail());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
