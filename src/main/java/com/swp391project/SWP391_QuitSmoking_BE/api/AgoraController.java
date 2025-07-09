package com.swp391project.SWP391_QuitSmoking_BE.api;



import com.swp391project.SWP391_QuitSmoking_BE.repository.AppointmentRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.AgoraService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/agora")
@RestController
@SecurityRequirement(name = "user_api")
public class AgoraController {
    private static final Logger log = LoggerFactory.getLogger(AgoraController.class);
    private final AgoraService agoraService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AppointmentRepository appointmentRepository;

    @Operation(summary = "Lấy token Agora cho cuộc hẹn",
               description = "Trả về token Agora cho cuộc hẹn dựa trên ID cuộc hẹn và quyền của người dùng (publisher/subscriber).")
    @GetMapping("/token/{appointmentId}")
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'COACH')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAgoraToken(
            @PathVariable Long appointmentId,
            @RequestParam(defaultValue = "true") boolean isPublisher,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID currentLoggedInUserId = userService.getUserIdFromUserDetails(userDetails);
            // Thời gian sống của token (ví dụ: 3600 giây = 1 giờ)
        int tokenDurationInSeconds = 3600; // 1 hour

        try {
            // Gọi service để tạo token Agora
            Map<String, String> tokenInfo = agoraService.generateAgoraToken(
                    appointmentId,
                    currentLoggedInUserId,
                    isPublisher,
                    tokenDurationInSeconds
            );
            return ResponseEntity.ok(ApiResponse.success(tokenInfo, "Agora token generated successfully."));
        } catch (RuntimeException e) {
            // Trả về lỗi nếu có vấn đề trong quá trình tạo token
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            // Bắt tất cả các lỗi khác và trả về thông báo lỗi
            log.error("Unexpected error while generating Agora token", e);
            return ResponseEntity.status(500).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while generating Agora token."));
        }
    }
}
