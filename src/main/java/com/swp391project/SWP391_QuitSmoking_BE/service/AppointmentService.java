package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentUserResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppointmentService {
    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository appointmentRepository;
    private final MemberRepository memberRepository;
    private final CoachRepository coachRepository;
    private final TemplateEngine templateEngine;
    private ModelMapper modelMapper;
    private final CoachScheduleRepository coachScheduleRepository; // Cần để cập nhật trạng thái CoachSchedule
    private final EmailService emailService; // Thêm EmailService để gửi email notification

    @Transactional
    public AppointmentResponseDTO createAppointment(UUID callerId, List<Role> callerRoles, AppointmentRequestDTO request) {
        Member memberToBook;
        Coach coachOfSchedule = null;

        // Xác định người gọi
        if (callerRoles.contains(Role.PREMIUM_MEMBER)) {
            // Nếu là member -> chỉ cho phép đặt lịch của chính mình
//            if (request.getMemberIdToBook() != null && !request.getMemberIdToBook().equals(callerId)) {
//                throw new AccessDeniedException("Bạn không có quyền đặt lịch cho thành viên khác.");
//            }
            memberToBook = memberRepository.findById(callerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + callerId));
        } else if (callerRoles.contains(Role.COACH)) {
//            if (request.getMemberIdToBook() == null) {
//                throw new IllegalArgumentException("Coach phải chỉ định Member ID để đặt lịch hộ.");
//            }
            if(request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email của Member không được để trống.");
            }

//            memberToBook = memberRepository.findById(request.getMemberIdToBook())
//                    .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + callerId));
            memberToBook = memberRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found with email: " + request.getEmail()));

            // Kiem tra role member, coach chi duoc dat lich cho Premium Member
            if (memberToBook.getUser().getRole() != Role.PREMIUM_MEMBER) {
                throw new IllegalArgumentException("Bạn chỉ có thể đặt lịch cho Premium Member.");
            }

            // Kiểm tra xem Coach có quyền đặt lịch cho Member này không
        } else {
            throw new AccessDeniedException("Bạn không có quyền đặt lịch hẹn. Chỉ có Premium Member hoặc Coach mới có thể đặt lịch.");
        }

        CoachSchedule coachSchedule = coachScheduleRepository.findById(request.getCoachScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("CoachSchedule not found with ID: " + request.getCoachScheduleId()));

        // Kiểm tra CoachSchedule cos bi xoa ko
        if (coachSchedule.isDeleted()) {
            throw new ResourceNotFoundException("Lịch trình coach với id: " + request.getCoachScheduleId() + " đã bị xóa.");
        }

        LocalDate scheduleDate = coachSchedule.getScheduleDate();
        LocalTime startTime = coachSchedule.getTimeSlot().getStartTime();
        LocalDateTime appointmentTime = LocalDateTime.of(scheduleDate, startTime);

        // Kiểm tra xem lịch trình có hợp lệ không
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Lịch trình không hợp lệ. Vui lòng chọn lịch trong tương lai.");
        }

        // Kiểm tra xem lịch trình đã bị đặt chưa
        if (coachSchedule.isBooked()) {
            throw new IllegalStateException("Lịch trình này đã được đặt. Vui lòng chọn lịch khác.");
        }
        // Tạo Appointment
        Appointment appointment = new Appointment();
        appointment.setMember(memberToBook);
        appointment.setCoachSchedule(coachSchedule);
        appointment.setStatus(AppointmentStatus.CONFIRMED); // Mặc định là CONFIRMED khi đặt thành công
        appointment.setNote(request.getNote());
        appointment.setBookingTime(appointmentTime);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        // Tao agora channel name cho appointment (format: appointment_ + UUID.randomUUID())
        appointment.setAgoraChannelName("appointment_" + UUID.randomUUID().toString().replace("-", ""));
//
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Cập nhật trạng thái isBooked của CoachSchedule
        coachSchedule.setBooked(true);
        coachScheduleRepository.save(coachSchedule);

        AppointmentResponseDTO dto = modelMapper.map(savedAppointment, AppointmentResponseDTO.class);
        if (appointment.getCoachSchedule() != null &&
                appointment.getCoachSchedule().getCoach() != null &&
                appointment.getCoachSchedule().getCoach().getUser() != null) {

            User coachUser = appointment.getCoachSchedule().getCoach().getUser();
            dto.getCoachSchedule().getCoach().setUsername(coachUser.getUsername());
            dto.getCoachSchedule().getCoach().setEmail(coachUser.getEmail());
        }

        return dto;
    }

    /**
     * Hủy một lịch hẹn
     *
     * @param appointmentId Id của lịch hẹn
     * @param currentUserId ID của người dùng hiện tại (để kiểm tra quyền)
     * @return AppointmentResponseDTO đã được hủy
     */
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long appointmentId, UUID currentUserId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));


        UUID memberId = appointment.getMember().getMemberId();
//        boolean isMemberCancelling = memberId.equals(currentUserId);

        boolean isMemberWhoBooked = appointment.getMember().getUser().getUserId().equals(currentUserId);
        boolean isCoachWhoOwnsSchedule = appointment.getCoachSchedule().getCoach().getUser().getUserId().equals(currentUserId);

        if (!isMemberWhoBooked && !isCoachWhoOwnsSchedule) {
            throw new AccessDeniedException("Bạn không có quyền hủy lịch hẹn này");
        }

        // Lấy thời gian hiện tại và thời gian hẹn
        LocalDate scheduleDate = appointment.getCoachSchedule().getScheduleDate();
        LocalTime startTime = appointment.getCoachSchedule().getTimeSlot().getStartTime();
        LocalTime endTime = appointment.getCoachSchedule().getTimeSlot().getEndTime();
        LocalDateTime appointmentStartTime = LocalDateTime.of(scheduleDate, startTime);
        LocalDateTime appointmentEndTime = LocalDateTime.of(scheduleDate, endTime);

        // Thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra trạng thái hiện tại của lịch hẹn
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED ||
                appointment.getStatus() == AppointmentStatus.MISSED) {
            throw new IllegalStateException("Không thể hủy lịch hẹn ở trạng thái hiện tại (" + appointment.getStatus() + ").");
        }


        // Nếu member -> ktra quy tắc 2h
        if (isMemberWhoBooked) {
            long hourUntilAppointment = Duration.between(now, appointmentStartTime).toHours();
            if (hourUntilAppointment < 2) {
                throw new IllegalStateException("Bạn chỉ có thể hủy lịch hẹn ít nhất 2 tiếng trước thời gian hẹn.");
            }
        } else {
            if (now.isAfter(appointmentStartTime) && now.isBefore(appointmentEndTime)) {
                throw new IllegalStateException("Coach không thể hủy lịch hẹn khi cuộc hẹn đang diễn ra.");
            }
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // isBooked của CoachSchedule sẽ được set lại là false
        CoachSchedule coachSchedule = updatedAppointment.getCoachSchedule();
        coachSchedule.setBooked(false);
        coachScheduleRepository.save(coachSchedule);

        // Gửi email notification nếu coach hủy appointment của premium member
        if (isCoachWhoOwnsSchedule) {
            sendCancellationEmailToMember(updatedAppointment);
        }

        AppointmentResponseDTO dto = modelMapper.map(updatedAppointment, AppointmentResponseDTO.class);
        if (appointment.getCoachSchedule() != null &&
                appointment.getCoachSchedule().getCoach() != null &&
                appointment.getCoachSchedule().getCoach().getUser() != null) {

            User coachUser = appointment.getCoachSchedule().getCoach().getUser();
            dto.getCoachSchedule().getCoach().setUsername(coachUser.getUsername());
            dto.getCoachSchedule().getCoach().setEmail(coachUser.getEmail());
        }

        return dto;
    }

    /**
     * Update trạng thái lịch hẹn (chỉ Admin hoặc Coach)
     *
     * @param appointmentId Id của lịch hẹn
     * @param newStatus     Trạng thái mới
     * @param currentUserId id của người dùng hiện tại (kiểm tra coach/admin)
     * @param isAdmin       Là Admin hay không
     * @return AppointmentResponseDTO đã cập nhật
     */
    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus, UUID currentUserId, boolean isAdmin) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        boolean isCoach = appointment.getCoachSchedule().getCoach().getCoachId().equals(currentUserId);
        if (!isAdmin && !isCoach) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật trạng thái lịch hẹn này.");
        }

        AppointmentStatus currentStatus = appointment.getStatus();

        // Các trạng thái cuối cùng (CANCELLED, COMPLETED, MISSED) không thể chuyển sang trạng thái khác
        if ((currentStatus == AppointmentStatus.CANCELLED ||
                currentStatus == AppointmentStatus.COMPLETED ||
                currentStatus == AppointmentStatus.MISSED) && newStatus != currentStatus) {
            throw new IllegalStateException("Không thể chuyển trạng thái từ '" + currentStatus + "' sang trạng thái khác vì nó là trạng thái cuối cùng.");
        }

        // Xử lý các chuyển đổi từ trạng thái CONFIRMED
        if (currentStatus == AppointmentStatus.CONFIRMED) {
            if (newStatus != AppointmentStatus.COMPLETED &&
                    newStatus != AppointmentStatus.MISSED &&
                    newStatus != AppointmentStatus.CANCELLED) {
                throw new IllegalStateException("Lịch hẹn ở trạng thái CONFIRMED chỉ có thể chuyển sang COMPLETED, MISSED hoặc CANCELLED.");
            }
        }

        //        Validation dựa trên thời gian cuộc hẹn
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getCoachSchedule().getScheduleDate(),
                appointment.getCoachSchedule().getTimeSlot().getStartTime()
        );

        if (newStatus == AppointmentStatus.CONFIRMED && appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Không thể xác nhận lịch hẹn đã qua thời gian bắt đầu.");
        }

        // Nếu cố gắng chuyển sang COMPLETED/MISSED mà thời gian chưa đến
        if ((newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.MISSED) && appointmentDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Chỉ có thể đánh dấu hoàn thành hoặc bỏ lỡ sau khi thời gian lịch hẹn đã trôi qua.");
        }

        // Nếu trạng thái mới là CANCELLED và trạng thái cũ không phải CANCELLED -> cập nhật trạng thái isBooked của CoachSchedule
        if (newStatus == AppointmentStatus.CANCELLED && currentStatus != AppointmentStatus.CANCELLED) {
            CoachSchedule coachSchedule = appointment.getCoachSchedule();
            coachSchedule.setBooked(false);
            coachScheduleRepository.save(coachSchedule);
            
            // Gửi email notification nếu coach hủy appointment của premium member
            if (isCoach) {
                sendCancellationEmailToMember(appointment);
            }
        }

        appointment.setStatus(newStatus);
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(updatedAppointment, AppointmentResponseDTO.class);
    }

    /**
     * Lấy tất cả lịch hẹn của một Member, có phân trang và lọc theo khoảng ngày.
     *
     * @param memberId  ID của thành viên.
     * @param startDate Ngày bắt đầu.
     * @param endDate   Ngày kết thúc.
     * @param pageable  Thông tin phân trang.
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
     *
     * @param coachId   ID của coach.
     * @param startDate Ngày bắt đầu.
     * @param endDate   Ngày kết thúc.
     * @param statuses  Danh sách trạng thái muốn lọc (nếu null/empty thì lấy tất cả).
     * @param pageable  Thông tin phân trang.
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
     *
     * @param startDate Ngày bắt đầu.
     * @param endDate   Ngày kết thúc.
     * @param statuses  Danh sách trạng thái muốn lọc.
     * @param pageable  Thông tin phân trang.
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
     *
     * @param memberId ID của thành viên.
     * @param limit    Số lượng lịch hẹn muốn lấy.
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
     *
     * @param coachId ID của coach.
     * @param limit   Số lượng lịch hẹn muốn lấy.
     * @return Danh sách AppointmentResponseDTO.
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getUpcomingCoachAppointments(UUID coachId, int limit) {
        List<AppointmentStatus> activeStatuses = List.of(AppointmentStatus.CONFIRMED);
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

    /**
     * Gửi email thông báo hủy lịch hẹn đến thành viên premium khi coach hủy appointment
     *
     * @param appointment Appointment đã bị hủy bởi coach
     */
    private void sendCancellationEmailToMember(Appointment appointment) {
        try {
            // Lấy thông tin member và coach
            Member member = appointment.getMember();
            Coach coach = appointment.getCoachSchedule().getCoach();
            User memberUser = member.getUser();
            User coachUser = coach.getUser();

            // Kiểm tra xem member có phải là premium không
            if (!member.getUser().getRole().equals(Role.PREMIUM_MEMBER)) {
                // Chỉ gửi email cho premium members
                return;
            }

            // Format thời gian appointment
            LocalDate scheduleDate = appointment.getCoachSchedule().getScheduleDate();
            LocalTime startTime = appointment.getCoachSchedule().getTimeSlot().getStartTime();
            LocalTime endTime = appointment.getCoachSchedule().getTimeSlot().getEndTime();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String formattedDate = scheduleDate.format(dateFormatter);
            String formattedTime = startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);


            // Chuẩn bị các biến cho template
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("memberName", memberUser.getUsername());
            templateVariables.put("coachName", coachUser.getCoach().getFullName() != null ? coachUser.getCoach().getFullName() : coachUser.getUsername());
            templateVariables.put("appointmentDate", formattedDate);
            templateVariables.put("appointmentTime", formattedTime);
            templateVariables.put("appointmentId", appointment.getAppointmentId());
            templateVariables.put("rescheduleLink", "https://localhost:5173/booking"); // Thay thế bằng URL đặt lịch lại thực tế

            // Tạo EmailDetail với tên template và biến template
            String subject = "Thông báo hủy lịch hẹn - QuitSmoking";
            EmailDetail emailDetail = new EmailDetail(
                    memberUser.getEmail(),
                    subject,
                    null, // Body để null, vì nội dung sẽ được tạo từ template
                    "cancellationEmail.html", // Tên template
                    templateVariables // Các biến cho template
            );

            // Gửi email
            emailService.sendEmail(emailDetail);

            log.info("✅ Đã gửi email thông báo hủy lịch hẹn tới: {}", memberUser.getEmail());

        } catch (Exception e) {
            // Log lỗi nhưng không throw exception để không ảnh hưởng đến flow chính
            log.error("❌ Lỗi khi gửi email thông báo hủy lịch hẹn: {}", e.getMessage());
        }
    }
}
