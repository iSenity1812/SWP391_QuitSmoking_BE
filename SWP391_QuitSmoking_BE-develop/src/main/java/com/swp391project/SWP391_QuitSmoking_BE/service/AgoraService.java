package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Appointment;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AppointmentRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import io.agora.media.RtcTokenBuilder2;
import io.agora.media.RtcTokenBuilder2.Role;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AgoraService {
    private final UserRepository userRepository;
    @Value("${agora.app-id}")
    private String appId;

    @Value("${agora.app-certificate}")
    private String appCertificate;

    private final AppointmentRepository appointmentRepository;

    /**
     * Tạo Agora Token cho RTC (Video/Audio) và RTM (Chat)
     * Sử dụng buildTokenWithRtm2 để tạo cả hai loại token cùng lúc.
     *
     * @param appointmentId ID cuộc hẹn để lấy channel name
     * @param currentLoggedInUserId User ID của người tham gia (member hoặc coach), có thể là 0 nếu Agora tự gán
     * @param isPublisher   True nếu người dùng có quyền publish (mic/cam), False nếu chỉ xem
     * @param tokenDurationInSeconds Thời gian sống của token RTC và RTM (ví dụ: 3600 giây = 1 giờ)
     * @return Map chứa Agora App ID, Channel Name, RTC Token, RTM Token
     */
    public Map<String, String> generateAgoraToken(Long appointmentId, UUID currentLoggedInUserId, boolean isPublisher, int tokenDurationInSeconds) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));

        User user = userRepository.findById(currentLoggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + currentLoggedInUserId));

        // --- KIỂM TRA QUYỀN NGƯỜI DÙNG ---
        // 1. Kiểm tra xem người dùng có phải là Premium Member hoặc Coach ko
        boolean isPremiumMember = user.getRole() == com.swp391project.SWP391_QuitSmoking_BE.enums.Role.PREMIUM_MEMBER;
        boolean isCoach = user.getRole() == com.swp391project.SWP391_QuitSmoking_BE.enums.Role.COACH;

        if (!isPremiumMember && !isCoach) {
            throw new RuntimeException("Bạn không có quyền tham gia cuộc họp. Chỉ có Premium Member và Coach mới được phép.");
        }

        // 2. Kiểm tra xem người dùng có phải là thành viên của cuộc hẹn này không
        boolean isParticipantOfThisAppointment = false;

        // Nếu người dùng là member
        if (user.getMember() != null) {
            if (appointment.getMember() != null && user.getMember().getMemberId().equals(appointment.getMember().getMemberId())) {
                isParticipantOfThisAppointment = true;
            }
        }

        // Nếu người dùng là coach
        if (user.getCoach() != null) {
            if (appointment.getCoachSchedule() != null && user.getCoach().getCoachId() != null &&
                    user.getCoach().getCoachId().equals(appointment.getCoachSchedule().getCoach().getCoachId())) {
                isParticipantOfThisAppointment = true;
            }
        }

        if (!isParticipantOfThisAppointment) {
            throw new RuntimeException("Bạn không phải là thành viên của cuộc hẹn này.");
        }

        // Logic kiêểm tra thời gian tham gia cuộc hẹn (cho phép join khi cuộc hẹn sắp đến 10p hoặc đang diễn ra)

        /*LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentStartTime = appointment.getBookingTime();
        int meetingDurationMinutes = 60;

        LocalDateTime appointmentEndTime = appointmentStartTime.plusMinutes(meetingDurationMinutes);

        // Cho phép người dùng tham gia 10p trước thời gian bắt đầu
        LocalDateTime joinableStartTime = appointmentStartTime.minusMinutes(10);
        // Ko cho phép tham gia sau khi cuộc hẹn kết thúc (cho phép join thêm 5p)
        LocalDateTime joinableEndTime = appointmentEndTime.plusMinutes(5);

        if (now.isBefore(joinableStartTime)) {
            throw new RuntimeException("Cuộc hẹn chưa bắt đầu. Bạn có thể tham gia từ " +
                    joinableStartTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + ".");
        }
        if (now.isAfter(joinableEndTime)) {
            throw new RuntimeException("Cuộc hẹn đã kết thúc.");
        }*/

        // --------------------------


        // --- TẠO TOKEN AGORA ---
        String channelName = appointment.getAgoraChannelName();
        if (channelName == null || channelName.isEmpty()) {
            throw new RuntimeException("Agora Channel Name not set for this appointment.");
        }

        // Dùng UUID của User làm "account" cho Agora (dạng String).
        // Đây sẽ là định danh của người dùng trong Agora thay vì một số int.
        String agoraUserAccount = user.getUserId().toString(); // Hoặc user.getUsername() nếu bạn muốn tên người dùng

        // RTC Role
        RtcTokenBuilder2.Role rtcRole = isPublisher ? Role.ROLE_PUBLISHER : Role.ROLE_SUBSCRIBER;

        // Thời gian hiện tại tính bằng giây
        int currentTimestamp = (int) Instant.now().getEpochSecond();
        // Thời gian hết hạn của token
        int expireTime = currentTimestamp + tokenDurationInSeconds;

        // Thời gian hết hạn cho từng đặc quyền riêng biệt (nếu muốn phân chia chi tiết hơn)
        // Ở đây ta dùng cùng thời gian hết hạn với token chính để đơn giản
        int joinChannelPrivilegeExpire = expireTime;
        int pubAudioPrivilegeExpire = expireTime;
        int pubVideoPrivilegeExpire = expireTime;
        int pubDataStreamPrivilegeExpire = expireTime;
        int rtmTokenExpire = expireTime;

        RtcTokenBuilder2 tokenBuilder = new RtcTokenBuilder2();

        // Sử dụng buildTokenWithRtm2 để tạo cả RTC và RTM token
        String token = tokenBuilder.buildTokenWithRtm2(
                appId,
                appCertificate,
                channelName,
                agoraUserAccount, // Dùng agoraUserAccount cho RTC
                rtcRole,
                expireTime, // rtcTokenExpire
                joinChannelPrivilegeExpire,
                pubAudioPrivilegeExpire,
                pubVideoPrivilegeExpire,
                pubDataStreamPrivilegeExpire,
                agoraUserAccount, // Dùng agoraUserAccount cho RTM
                rtmTokenExpire
        );

        Map<String, String> response = new HashMap<>();
        response.put("agoraAppId", appId);
        response.put("agoraChannelName", channelName);
        response.put("agoraToken", token); // Token này chứa cả RTC và RTM privileges
        // Bạn có thể trả về UID đã dùng để frontend biết (nếu cần)
        response.put("agoraUid", agoraUserAccount);

        return response;
    }
}
